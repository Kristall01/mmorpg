import { ColoredCloth } from "game/graphics/renderers/world/HumanRenderer";
import {IEventReciever, ModelEventType, SignalIn} from "model/Definitions";
import LogicModel from "model/LogicModel";
import SignalAttack from "model/signals/SignalAttack";
import SignalChangeClothes from "model/signals/SignalChangeClothes";
import SignalChangeHp from "model/signals/SignalChangeHp";
import SignalChat from "model/signals/SignalChat";
import SignalCloseInventory from "model/signals/SignalCloseInventory";
import SignalDespawnItem from "model/signals/SignalDespawnItem";
import SignalDied from "model/signals/SignalDied";
import SignalEntityDeath from "model/signals/SignalEntityDeath";
import SignalEntityDespawn from "model/signals/SignalEntityDespawn";
import SignalEntitypath from "model/signals/SignalEntitypath";
import SignalEntityspawn from "model/signals/SignalEntityspawn";
import SignalEntityspeed from "model/signals/SignalEntityspeed";
import SignalEntityTeleport from "model/signals/SignalEntityTeleport";
import SignalFocus from "model/signals/SignalFocus";
import SignalInPortalspawn from "model/signals/SignalInPortalspawn";
import SignalInSpawnItem from "model/signals/SignalInSpawnItem";
import SignalJoinworld from "model/signals/SignalJoinworld";
import SignalLabelFor from "model/signals/SignalLabel";
import SignalLeaveworld from "model/signals/SignalLeaveworld";
import SignalOpenInventory from "model/signals/SignalOpenInventory";
import SignalRenameEntity from "model/signals/SignalRenameEntity";
import SignalSetinventory from "model/signals/SignalSetinventory";
import SignalSound from "model/signals/SignalSound";
import SignalSudo from "model/signals/SignalSudo";
import FloatingItem from "visual_model/FloatingItem";
import Item from "visual_model/Item";
import ItemStack from "visual_model/ItemStack";
import { LabelType } from "visual_model/Label";
import { Position } from "visual_model/VisualModel";
//import SignalOut from "model/signals/SignalOut";

class NetworkModel extends LogicModel {

	private packetMap: Map<string, (data:any) => void> = new Map();

	private ws: WebSocket = null!
	private pingUpdateTask: number | null = null
	private pingPromiseAccept: (a:number) => void = null!;
	private endSent = false
	private connectionOpened = false

	public pingDelay: number

	constructor(callback: IEventReciever, connectionURL: string, name: string) {
		super(callback);

		this.addPacketSignal("chat", ({message}) => new SignalChat(message));
		this.addPacketSignal("entitypath", ({id, startNanos, points}) => new SignalEntitypath(id, (startNanos - this.pingDelay)/1000000, points));
		this.addPacket("authenticated", () => {
			this.broadcastEvent({type: ModelEventType.PLAY});
			this.recalcPingDelay();
		});

		this.addPacket("pong", ({time, id}) => this.handlePong(time, id));
		this.addPacket("disconnect", ({reason}) => this.endConnection("Ki lettél rúgva a szerverről: "+reason));
		this.addPacketSignal("moveentity", ({x,y, id, startNanos}) => {
			let points: Position[] = [];
			for(let i = 0; i < x.length; ++i) {
				points.push([x[i],y[i]]);
			}
 			return new SignalEntitypath(id, this.convertServerNanos(startNanos), points);
		})
		this.addPacketSignal("spawnentity", ({x, y, ID, speed, type, hp, maxHp}) => new SignalEntityspawn(ID, type, [x,y], speed, hp, maxHp));
		this.addPacketSignal("despawnentity", ({id}) => new SignalEntityDespawn(id));
		this.addPacketSignal("followentity", ({id}) => new SignalFocus(id));
		this.addPacketSignal("entityspeed", ({id, speed}) => new SignalEntityspeed(id, speed));
		this.addPacketSignal("joinworld", ({tileGrid, width, height, spawnX, spawnY}) => new SignalJoinworld(spawnX, spawnY, width, height, tileGrid));
		this.addPacketSignal("leaveworld", () => new SignalLeaveworld());
		this.addPacketSignal("entityrename", ({id, newname}) => new SignalRenameEntity(id, newname));
		this.addPacketSignal("clothes", ({clothes, id}) => new SignalChangeClothes(id, clothes));
		this.addPacketSignal("hpchange", ({id, newHp}) => new SignalChangeHp(id, newHp));
		this.addPacketSignal("labelFor", ({text, labelType, entityID}) => {
			let type = LabelType.enum.values[labelType];
			return new SignalLabelFor(text, type, entityID);
		});
		this.addPacketSignal("entityDeath", ({id}) => new SignalEntityDeath(id));
		this.addPacketSignal("died", () => new SignalDied());
		this.addPacketSignal("portal-spawn", ({X, Y, radius}) => new SignalInPortalspawn(X, Y, radius));
		this.addPacketSignal("spawn-item", ({x,y,id,item}) => new SignalInSpawnItem(new FloatingItem(id, [x,y], new Item(item.type, item.material, item.name, item.description, item.flags))));
		this.addPacketSignal("despawn-item", ({id}) => new SignalDespawnItem(id));
		this.addPacketSignal("setinventory", ({items, inventoryID}) => {
			let itemStacks: Array<ItemStack> = [];
			for(let {amount, item} of items) {
				let {name, description, material, flags, type} = item;
				itemStacks.push({amount, item: new Item(type, material, name, description, flags)})
			}
			return new SignalSetinventory(itemStacks, inventoryID);
		});
		this.addPacketSignal("teleport", ({x,y,entityID, instant}) => new SignalEntityTeleport(x,y,entityID,instant));
		this.addPacketSignal("attack", ({x,y,entityID}) => new SignalAttack(entityID, x,y));
		this.addPacketSignal("sudo", ({text}) => new SignalSudo(text));
		this.addPacketSignal("sound", ({soundID}) => new SignalSound(soundID));
		this.addPacketSignal("open-inventory", ({inventoryID}) => new SignalOpenInventory(inventoryID));
		this.addPacketSignal("close-inventory", () => new SignalCloseInventory());

		//this.register("entitypath", ({id, startNanos, points}) => new SignalEntitypath(id, (startNanos - netModel.pingDelay)/1000000, points))


		this.pingDelay = Date.now()*100000 - this.nowNanos(); //calculate using 0 ping and matching clock

		const connectErrorMessage = "kapcsolódási hiba: nem sikerült kapcsolódni a szerverhez";

		try {
			this.ws = new WebSocket(connectionURL);
		}
		catch(err) {
			console.error(err);
			this.endConnection(connectErrorMessage);
			return;
		}

		this.ws.addEventListener("open", () => {
			this.sendPacket("auth", {name: name});
			this.connectionOpened = true;
			this.broadcastEvent({type: ModelEventType.CONNECTED});
			//this.recalcPingDelay();
		});

		this.ws.addEventListener("error", (e) => {
			if(!this.connectionOpened) {
				this.endConnection(connectErrorMessage);
			}
			else {
				this.endConnection("kapcsolati hiba: a websocket kapcsolat váratlanul megszakadt");
			}
		})

		this.ws.addEventListener("message", (e) => {
			let rawPacketData: string = e.data;
			let type: string, data;
			try {
				let separatorIndex = rawPacketData.indexOf(';') ;
				if(separatorIndex === -1) {
					throw new Error("ilelgal packet format");
				}
				type = rawPacketData.substring(0, separatorIndex);
				data = JSON.parse(rawPacketData.substring(separatorIndex+1));
			}
			catch(err) {
				this.endConnection("kommunikációs hiba: a szerver hibás formátumú csomagot küldött");
				return;
			}
			let a = this.packetMap.get(type);
			if(a === undefined) {
				console.warn("kommunikációs hiba: a szerver ismeretlen típusú csomagot küldött ("+type+")");
				//this.endConnection("protocol error: packet sent by server has invalid type ("+type+")");
				return;
			}
			a(data);
		})

		this.ws.addEventListener("close", () => {
			this.endConnection("hálózati hiba: a szerver váratlanul megszűntette a kapcsolatot");
		});
	}

	inventoryInteract(type: string, inventoryID: string): void {
		this.sendPacket("inventory-interact", {inventoryID, type})
	}

	attackTowards(x: number, y: number) {
		this.sendPacket("attack", {x, y});
	}

	private convertServerNanos(nanos: number) {
		return (nanos - this.pingDelay)/1000000;
	}

	private handlePong(time: number, id: number) {
		this.pingPromiseAccept(time);
	}

	private addPacket(type: string, action: (a:any) => void) {
		this.packetMap.set(type, action);
	}

	private addPacketSignal(type: string, action: (a:any) => SignalIn) {
		this.packetMap.set(type, d => this.broadcastSignal(action(d)));
	}

	private endConnection(message?: string) {
		if(this.endSent) {
			return;
		}
		this.endSent = true;
		if(this.pingUpdateTask !== null) {
			window.clearTimeout(this.pingUpdateTask);
			this.pingUpdateTask = null;
		}
		if(message === undefined) {
			this.broadcastEvent({type: ModelEventType.END});
		}
		else {
			this.broadcastEvent({type: ModelEventType.END, data: message});
		}
		try {
			this.ws.close();
		}
		catch(err) {}
	}

	private nowNanos() {
		return performance.now()*1000000;
	}

	private recalcPingDelay() {
		let p = new Promise<number>((accept,reject) => {
			this.pingPromiseAccept = accept;
		});
		let pingStartTime = this.nowNanos();
		p.then((serverNanos: number) => {
			let pingEndTime = this.nowNanos();
			let pingTime = (pingEndTime - pingStartTime);
			let servertime = serverNanos + pingTime/2;
			this.pingDelay = servertime - pingEndTime;
			this.pingUpdateTask = window.setTimeout(() => this.recalcPingDelay(), 1000);
		})
		this.sendPacket("ping", {});
	}

	sendChatMessage(message: string): void {
		this.sendPacket("chat", {message});
	}

	applyClothes(clothes: ColoredCloth[]): void {
		let jsonClothes = [];
		for(let c of clothes) {
			jsonClothes.push({
				color: c.color.id,
				type: c.cloth.id
			});
		}
		this.sendPacket("apply-clothes", {clothes: jsonClothes});
	}

	collectNearbyItems(): void {
		this.sendPacket("collect-items", {});
	}

	private sendPacket(type: string, data: any) {
		this.ws.send(type+';'+JSON.stringify(data));
	}

	disconnect() {
		this.endConnection();
	}

	moveMeTo(x: number, y: number): void {
		this.sendPacket("move", {x,y});
	}

}

export default NetworkModel;
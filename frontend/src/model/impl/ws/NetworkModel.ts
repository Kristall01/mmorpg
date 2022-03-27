import {IEventReciever, ModelEvent, ModelEventType, SignalIn} from "model/Definitions";
import LogicModel from "model/LogicModel";
import SignalChangeClothes from "model/signals/SignalChangeClothes";
import SignalChangeHp from "model/signals/SignalChangeHp";
import SignalChat from "model/signals/SignalChat";
import SignalDespawnItem from "model/signals/SignalDespawnItem";
import SignalDied from "model/signals/SignalDied";
import SignalEntityDeath from "model/signals/SignalEntityDeath";
import SignalEntityDespawn from "model/signals/SignalEntityDespawn";
import SignalEntitypath from "model/signals/SignalEntitypath";
import SignalEntityspawn from "model/signals/SignalEntityspawn";
import SignalEntityspeed from "model/signals/SignalEntityspeed";
import SignalFocus from "model/signals/SignalFocus";
import SignalInPortalspawn from "model/signals/SignalInPortalspawn";
import SignalInSpawnItem from "model/signals/SignalInSpawnItem";
import SignalJoinworld from "model/signals/SignalJoinworld";
import SignalLabelFor from "model/signals/SignalLabel";
import SignalLeaveworld from "model/signals/SignalLeaveworld";
import SignalRenameEntity from "model/signals/SignalRenameEntity";
import SignalSetinventory from "model/signals/SignalSetinventory";
import FloatingItem from "visual_model/FloatingItem";
import Item from "visual_model/Item";
import ItemStack from "visual_model/ItemStack";
import { LabelType } from "visual_model/Label";
import { Position } from "visual_model/VisualModel";
//import SignalOut from "model/signals/SignalOut";

class NetworkModel extends LogicModel {

	private packetMap: Map<string, (data:any) => void> = new Map();

	private ws: WebSocket
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
		this.addPacket("disconnect", ({reason}) => this.endConnection("disconnected from server: "+reason));
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
		this.addPacketSignal("spawn-item", ({x,y,type,id,name}) => new SignalInSpawnItem(new FloatingItem(id, [x,y],new Item(type, name ?? undefined))));
		this.addPacketSignal("despawn-item", ({id}) => new SignalDespawnItem(id));
		this.addPacketSignal("setinventory", ({items}) => {
			let itemStacks: Array<ItemStack> = [];
			for(let {amount, item} of items) {
				let {type, name} = item;
				itemStacks.push({amount, item: new Item(type, name ?? undefined)})
			}

			return new SignalSetinventory(itemStacks);
		});

		//this.register("entitypath", ({id, startNanos, points}) => new SignalEntitypath(id, (startNanos - netModel.pingDelay)/1000000, points))


		this.pingDelay = Date.now()*100000 - this.nowNanos(); //calculate using 0 ping and matching clock

		this.ws = new WebSocket(connectionURL);

		this.ws.addEventListener("open", () => {
			this.sendPacket("auth", {name: name});
			this.connectionOpened = true;
			this.broadcastEvent({type: ModelEventType.CONNECTED});
			//this.recalcPingDelay();
		});

		this.ws.addEventListener("error", (e) => {
			if(!this.connectionOpened) {
				this.endConnection("websocket error: failed to connect to server");
			}
			else {
				this.endConnection("websocket error: WebSocket connection called error event");
			}
		})

		this.ws.addEventListener("message", (e) => {
			let rawPacketData = e.data;
			let type, data;
			try {
				let parsedPacket = JSON.parse(rawPacketData);
				type = parsedPacket.type;
				data = parsedPacket.data;
			}
			catch(err) {
				this.endConnection("protocol error: packet sent by server has invalid format");
				return;
			}
			let a = this.packetMap.get(type);
			if(a === undefined) {
				console.warn("protocol error: packet sent by server has invalid type ("+type+")");
				//this.endConnection("protocol error: packet sent by server has invalid type ("+type+")");
				return;
			}
			a(data);
		})

		this.ws.addEventListener("close", () => {
			this.endConnection("network error: connection closed prematurely")
		});
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
		console.log("ending connection with message:",message);
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
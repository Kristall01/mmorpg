import Matrix from "Matrix";
import {IEventReciever, ModelEvent, ModelEventType} from "model/Definitions";
import LogicModel from "model/LogicModel";
import SignalChangeClothes from "model/signals/SignalChangeClothes";
import SignalChangeHp from "model/signals/SignalChangeHp";
import SignalChat from "model/signals/SignalChat";
import SignalClarchat from "model/signals/SignalClearchat";
import SignalEntitypath from "model/signals/SignalEntitypath";
import SignalEntityspawn from "model/signals/SignalEntityspawn";
import SignalFocus from "model/signals/SignalFocus";
import SignalJoinworld from "model/signals/SignalJoinworld";
import SignalRenameEntity from "model/signals/SignalRenameEntity";
import Level from "visual_model/Level";
import { ConstStatus, Direction, StatusFn, zigzagStatus } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";
//import SignalOut from "model/signals/SignalOut";

const netLag = 0;
const startPos: Position = [0,0];


let level = new Level(50, 8);
let m = level.addLayer();
m.fill(([x,y]) => {
	if(x == 0 || x == level.width-1 || y == 0 || y == level.height-1) {
		return "WATER";
	}
	else {
		return "GRASS";
	}
});

class DModel extends LogicModel {

	private name: string;
	private statusFn: StatusFn
	private tiles: Matrix<string | null>
	private entitySpeed: number = 2;

	constructor(callback: IEventReciever, username: string) {
		super(callback);
		let {speed, tilegrid} = {speed: 5, tilegrid: m};
		this.tiles = tilegrid!
		this.name = username;
		this.entitySpeed = speed!;
		this.statusFn = ConstStatus(startPos, Direction.enum.map.SOUTH);

		setTimeout(() => {
			this.broadcastEvent({type: ModelEventType.CONNECTED});
			setTimeout(() => {
				this.broadcastEvent({type: ModelEventType.PLAY});
				this.broadcastSignal(new SignalJoinworld(startPos[0], startPos[1], tilegrid!.width, tilegrid!.height, level));
				this.broadcastSignal(new SignalChat("§eÜdv a chaten, "+this.name+"!"));
				this.broadcastSignal(new SignalChat("§eA chat megnyitásához nyomd meg az ENTER gombot!"));
				this.broadcastSignal(new SignalEntityspawn(0, "HUMAN", startPos, this.entitySpeed, 70, 100));
				this.broadcastSignal(new SignalRenameEntity(0, username));
				this.broadcastSignal(new SignalChangeClothes(0, ["SUIT", "PANTS_SUIT","SHOES"]));
				this.broadcastSignal(new SignalFocus(0));
			}, 1);
	
		}, 1);

	}

	/* sendSignal(signal: SignalOut) {
		if(signal.type === "chat") {
			this.broadcastSignal(Object.assign(new SignalInChat(), {message: signal.data.message}));
		}
	}; */

	sendChatMessage(message: string): void {
		if(message[0] !== '/') {
			this.broadcastSignal(Object.assign(new SignalChat("§a"+this.name+" §7»§r "+message)))
			return;
		}
		message = message.substring(1);
		let firstSpace = message.indexOf(' ');
		let prefix: string = undefined!;
		let args: string[] = undefined!
		if(firstSpace === -1) {
			prefix = message;
			args = [];
		}
		else {
			prefix = message.substring(0, firstSpace);
			args = message.substring(firstSpace+1).split(" ");
		}
		switch(prefix) {
			case "clearchat": {
				this.broadcastSignal(new SignalClarchat());
				this.broadcastSignal(new SignalChat("§7§oChat törölve"));
				break;
			}
			case "sethp": {
				if(args.length < 1) {
					this.broadcastSignal(new SignalChat("§cKevés paraméter"));
					return;
				}
				let hp: number;
				try {
					hp = parseInt(args[0]);
				}
				catch(err) {
					this.broadcastSignal(new SignalChat("Érvénytelen szám"));
					return;
				}
				if(hp < 0) {
					hp = 0;
				}
				else if(hp > 100) {
					hp = 100;
				}
				this.broadcastSignal(new SignalChangeHp(0, hp));
				break;
			}
			default: {
				this.broadcastSignal(new SignalChat("§cHiba: §4Nincs ilyen parancs."));
			}
		}
	}

	collectNearbyItems(): void {
		//noting :/
	}

	moveMeTo(x: number, y: number): void {
		setTimeout(() => {
			if(x > this.tiles.width) {
				x = this.tiles.width;
			}
			else if(x < 0) {
				x = 0;
			}
			if(y > this.tiles.height) {
				y = this.tiles.height;
			}
			else if(y < 0) {
				y = 0;
			}

			let t = performance.now();
			let currentPosition = this.statusFn(t)
			let path: Position[] = [currentPosition.position, [x,y]];
			this.statusFn = zigzagStatus(t, path, this.entitySpeed);
			setTimeout(() => {
				this.broadcastSignal(new SignalEntitypath(0, t, path));
			}, netLag/2);
		}, netLag/2);
	}

	disconnect() {
		this.broadcastEvent({type: ModelEventType.END});
	}

}

export default DModel;
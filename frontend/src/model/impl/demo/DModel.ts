import Matrix from "Matrix";
import {IEventReciever, ModelEvent, ModelEventType} from "model/Definitions";
import LogicModel from "model/LogicModel";
import SignalChangeClothes from "model/signals/SignalChangeClothes";
import SignalChat from "model/signals/SignalChat";
import SignalClarchat from "model/signals/SignalClearchat";
import SignalEntitypath from "model/signals/SignalEntitypath";
import SignalEntityspawn from "model/signals/SignalEntityspawn";
import SignalFocus from "model/signals/SignalFocus";
import SignalJoinworld from "model/signals/SignalJoinworld";
import SignalRenameEntity from "model/signals/SignalRenameEntity";
import { ConstStatus, Direction, StatusFn, zigzagStatus } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";
//import SignalOut from "model/signals/SignalOut";

const entitySpeed = 2;

const netLag = 50;
const startPos: Position = [0,0];

let camleak = false;



let map = {width: 50, height: 8};
let m = new Matrix<string>(50, 8);
m.fill(([x,y]) => {
	if(x == 0 || x == map.width-1 || y == 0 || y == map.height-1) {
		return "WATER";
	}
	else {
		return "GRASS";
	}
});

class DModel extends LogicModel {

	private name: string;
	private statusFn: StatusFn
	private tiles: Matrix<string>

	constructor(callback: IEventReciever, username: string, tilegrid: Matrix<string> = m) {
		super(callback);
		this.tiles = tilegrid;
		this.name = username;
		this.statusFn = ConstStatus(startPos, Direction.enum.map.SOUTH);

		setTimeout(() => {
			this.broadcastEvent({type: ModelEventType.CONNECTED});
			setTimeout(() => {
				this.broadcastEvent({type: ModelEventType.PLAY});
				this.broadcastSignal(new SignalJoinworld(startPos[0], startPos[1], tilegrid.width, tilegrid.height, tilegrid));
				this.broadcastSignal(new SignalChat("§eÜdv a chaten, "+this.name+"!"));
				this.broadcastSignal(new SignalChat("§eA chat megnyitásához nyomd meg az ENTER gombot!"));
				this.broadcastSignal(new SignalEntityspawn(0, "HUMAN", startPos, entitySpeed));
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
			default: {
				this.broadcastSignal(new SignalChat("§cHiba: §4Nincs ilyen parancs."));
			}
		}
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
			this.statusFn = zigzagStatus(t, path, entitySpeed);
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
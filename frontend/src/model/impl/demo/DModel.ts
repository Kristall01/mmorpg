import {IEventReciever, ModelEvent, ModelEventType} from "model/Definitions";
import LogicModel from "model/LogicModel";
import SignalChat from "model/signals/SignalChat";
import SignalClarchat from "model/signals/SignalClearchat";
import SignalEntitypath from "model/signals/SignalEntitypath";
import SignalEntityspawn from "model/signals/SignalEntityspawn";
import SignalFocus from "model/signals/SignalFocus";
import { ConstPath, EntityConstPath, LinearPath, PathFn, zigzagPath } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";
//import SignalOut from "model/signals/SignalOut";

const entitySpeed = 5;

const netLag = 100;
const startPos: Position = [5,5];

class DModel extends LogicModel {

	private name: string;
	private positionFn: PathFn

	constructor(callback: IEventReciever, username: string) {
		super(callback);
		this.name = username;
		this.positionFn = ConstPath(startPos);

		setTimeout(() => {
			//this.broadcastEvent({type: ModelEventType.END, data: "lololo"});
 			this.broadcastEvent({type: ModelEventType.PLAY});
			this.broadcastSignal(new SignalChat("§eÜdv a chaten, "+this.name+"!"));
			this.broadcastSignal(new SignalChat("§eA chat megnyitásához nyomd meg az ENTER gombot!"));
			this.broadcastSignal(new SignalEntityspawn(0, "test", startPos, entitySpeed));
			this.broadcastSignal(new SignalFocus(0));
 		}, 100);
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
			if(x > 25) {
				x = 25;
			}
			else if(x < 5) {
				x = 5;
			}
			if(y > 25) {
				y = 25;
			}
			else if(y < 5) {
				y = 5;
			}

			let t = performance.now();
			let currentPosition = this.positionFn(t)
			let path: Position[] = [currentPosition, [x,y]];
			this.positionFn = zigzagPath(t, path, entitySpeed);
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
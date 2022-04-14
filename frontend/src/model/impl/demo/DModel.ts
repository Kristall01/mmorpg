import parseText from "game/ui/chat/textparser";
import { ColoredCloth } from "game/graphics/renderers/world/HumanRenderer";
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
import { ConstStatus, Direction, StatusFn, zigzagStatus } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";
//import SignalOut from "model/signals/SignalOut";

const entitySpeed = 2;

const netLag = 50;
const startPos: Position = [5,5];

let camleak = false;

let tileGrid: string[] = [];
let map = {width: 50, height: 8};
for(let y = 0; y < map.height; ++y) {
	for(let x = 0; x < map.width; ++x) {
		if(x == 0 || x == map.width-1 || y == 0 || y == map.height-1) {
			tileGrid.push("WATER");
		}
		else {
			tileGrid.push("GRASS");
		}
	}
}


class DModel extends LogicModel {

	private name: string;
	private statusFn: StatusFn

	constructor(callback: IEventReciever, username: string) {
		super(callback);
		this.name = username;
		this.statusFn = ConstStatus(startPos, Direction.enum.map.SOUTH);

		setTimeout(() => {
			this.broadcastEvent({type: ModelEventType.CONNECTED});
			setTimeout(() => {
				this.broadcastEvent({type: ModelEventType.PLAY});
				this.broadcastSignal(new SignalJoinworld(startPos[0], startPos[1], map.width, map.height, tileGrid));
				this.broadcastSignal(new SignalChat("§eÜdv a chaten, "+this.name+"!"));
				this.broadcastSignal(new SignalChat("§eA chat megnyitásához nyomd meg az ENTER gombot!"));
				this.broadcastSignal(new SignalEntityspawn(0, "HUMAN", startPos, entitySpeed, 70, 100));
				this.broadcastSignal(new SignalRenameEntity(0, username));
				this.broadcastSignal(new SignalChangeClothes(0, [{cloth: "FLORAL", "color": "PINK"}, {cloth: "PANTS_SUIT",color: "BLACK"}, {cloth: "SHOES",color: "BLACK"}]));
				this.broadcastSignal(new SignalFocus(0));
			}, 1);
	
		}, 1);

	}

	applyClothes(clothes: ColoredCloth[]): void {
		this.broadcastSignal(new SignalChangeClothes(0, clothes.map(a => ({cloth: a.cloth.id, color: a.color.id}))));
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
			case "parse": {
				console.log(parseText(args[0]));
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
			if(x > map.width-1) {
				x = map.width-1;
			}
			else if(x < 1) {
				x = 1;
			}
			if(y > map.height-1) {
				y = map.height-1;
			}
			else if(y < 1) {
				y = 1;
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
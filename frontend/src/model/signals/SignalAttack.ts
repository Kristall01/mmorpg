import { SignalIn } from "model/Definitions";
import VisualModel, { Position } from "visual_model/VisualModel";

export default class SignalAttack implements SignalIn {

	private pos: Position
	private entityID: number;

	constructor(entityID: number, x: number, y: number) {
		this.pos = [x,y];
		this.entityID = entityID;
	}

	execute(model: VisualModel): void {
		let e = model.world?.getEntity(this.entityID);
		if(e === undefined) {
			console.warn("network error: entity of ",this.entityID," does not exist clientside.");
			return;
		}
		e.attack(this.pos);
	}

}

import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalChangeHp implements SignalIn {

	private entityID: number;
	private x: number;
	private y: number;
	private instant: boolean;

	constructor(x: number, y: number, entityID: number, instant: boolean) {
		this.x = x;
		this.y = y;
		this.entityID = entityID;
		this.instant = instant;
	}

	execute(model: VisualModel): void {
		let e = model.world?.getEntity(this.entityID);
		if(e === undefined) {
			console.warn("network error: entity of ",this.entityID," does not exist clientside.");
			return;
		}
		e.teleport([this.x, this.y], this.instant);
	}

}
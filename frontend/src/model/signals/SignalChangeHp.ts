import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalChangeHp implements SignalIn {

	private hp: number;
	private entityID: number;

	constructor(entityID: number, newHP: number) {
		this.entityID = entityID;
		this.hp = newHP;
	}

	execute(model: VisualModel): void {
		let e = model.world?.getEntity(this.entityID);
		if(e === undefined) {
			console.warn("network error: entity of ",this.entityID," does not exist clientside.");
			return;
		}
		e.hp = this.hp;
	}

}
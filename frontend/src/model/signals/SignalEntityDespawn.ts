import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalEntityDespawn implements SignalIn {

	id: number

	constructor(id: number) {
		this.id = id;
	}

	execute(model: VisualModel): void {
		model.world?.despawnEntiy(this.id);
	}

}
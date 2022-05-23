import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalDespawnItem implements SignalIn {

	private id: number;

	constructor(id: number) {
		this.id = id;
	}

	execute(model: VisualModel): void {
		let e = model.world?.despawnItem(this.id);
	}

}
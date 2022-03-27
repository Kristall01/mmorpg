import { SignalIn } from "model/Definitions";
import FloatingItem from "visual_model/FloatingItem";
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
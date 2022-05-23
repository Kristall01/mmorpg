import { SignalIn } from "model/Definitions";
import FloatingItem from "visual_model/FloatingItem";
import VisualModel from "visual_model/VisualModel";

export default class SignalInSpawnItem implements SignalIn {

	private item: FloatingItem;

	constructor(item: FloatingItem) {
		this.item = item;
	}

	execute(model: VisualModel): void {
		model.world?.spawnItem(this.item);
	}

}
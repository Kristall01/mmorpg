import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalCloseInventory implements SignalIn {

	execute(model: VisualModel): void {
		let w = model.world;
		if(w === null) {
			return;
		}
		w.closeInventory();
	}

}
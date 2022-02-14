import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalFocus implements SignalIn {

	private id: number

	constructor(id: number) {
		this.id = id;
	}

	execute(model: VisualModel) {
		model.world?.followEntity(this.id);
	}

}
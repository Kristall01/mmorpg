import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalDied implements SignalIn {

	constructor() {}

	execute(model: VisualModel): void {
		model.dead = true;
	}

}
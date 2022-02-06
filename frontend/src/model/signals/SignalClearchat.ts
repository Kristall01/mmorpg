import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalClarchat implements SignalIn {

	constructor() {}

	execute(model: VisualModel): void {
		model.clearChat();
	}

}
import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalChat implements SignalIn {

	message: string = null!

	constructor(message: string) {
		this.message = message;
	}

	execute(model: VisualModel): void {
		model.addChatEntry(this.message);
	}

}
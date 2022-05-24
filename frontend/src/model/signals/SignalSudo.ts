import { SignalIn } from "model/Definitions";
import ItemStack from "visual_model/ItemStack";
import VisualModel from "visual_model/VisualModel";

export default class SignalSetinventory implements SignalIn {

	private message: string;

	constructor(message: string) {
		this.message = message;
	}

	execute(model: VisualModel) {
		model.queueSudoCommand(this.message);
	}

}
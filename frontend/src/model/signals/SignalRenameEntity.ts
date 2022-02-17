import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalRenameEntity implements SignalIn {

	private id: number;
	private name: string | null;

	constructor(id: number, name: string | null) {
		this.id = id;
		this.name = name;
	}

	execute(model: VisualModel) {
		model.world?.getEntity(this.id)?.setName(this.name);
	}


}
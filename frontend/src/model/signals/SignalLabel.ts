import { SignalIn } from "model/Definitions";
import { LabelType } from "visual_model/Label";
import VisualModel from "visual_model/VisualModel";

export default class SignalLabelFor implements SignalIn {

	private text: string;
	private labelType: LabelType;
	private entityID: number;

	constructor(text: string, labelType: LabelType, entityID: number) {
		this.text = text;
		this.labelType = labelType;
		this.entityID = entityID;
	}

	execute(model: VisualModel): void {
		let entity = model.world?.getEntity(this.entityID);
		if(entity === undefined) {
			console.warn("network error: entity of ",this.entityID," does not exist clientside.");
			return;
		}
		model.showLabelFor(this.text, this.labelType, entity);
	}

}
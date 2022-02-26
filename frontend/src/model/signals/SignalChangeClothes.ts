import { SignalIn } from "model/Definitions";
import HumanEntity from "visual_model/entity/HumanEntity";
import VisualModel from "visual_model/VisualModel";

export default class SignalChangeClothes implements SignalIn {

	private clothes: string[];
	private entityID: number;

	constructor(entityID: number, clothes: string[]) {
		this.clothes = clothes;
		this.entityID = entityID;
	}

	execute(model: VisualModel): void {
		let e = model.world?.getEntity(this.entityID);
		if(e !== undefined && e instanceof HumanEntity) {
			e.changeClothes(this.clothes);
		}
	}

}

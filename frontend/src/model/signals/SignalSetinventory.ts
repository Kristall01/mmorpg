import { SignalIn } from "model/Definitions";
import ItemStack from "visual_model/ItemStack";
import VisualModel from "visual_model/VisualModel";

export default class SignalSetinventory implements SignalIn {

	private items: Array<ItemStack>;

	constructor(items: Array<ItemStack>) {
		this.items = items;
	}

	execute(model: VisualModel) {
		model.setInventory(this.items);
	}


}
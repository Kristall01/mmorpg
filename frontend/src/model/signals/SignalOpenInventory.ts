import { SignalIn } from "model/Definitions";
import ItemStack from "visual_model/ItemStack";
import VisualModel from "visual_model/VisualModel";

export default class SignalOpenInventory implements SignalIn {

	private inventoryID: string | undefined;

	constructor(inventoryID: string | undefined) {
		this.inventoryID = inventoryID;
	}

	execute(model: VisualModel) {
		let w = model.world;
		if(w === null) {
			return;
		}
		w.openInventory(this.inventoryID);
	}

}

import { SignalIn } from "model/Definitions";
import ItemStack from "visual_model/ItemStack";
import VisualModel from "visual_model/VisualModel";

export default class SignalSetinventory implements SignalIn {

	private items: Array<ItemStack>;
	private inventoryID: string;

	constructor(items: Array<ItemStack>, inventoryID: string) {
		this.items = items;
		this.inventoryID = inventoryID;
	}

	execute(model: VisualModel) {
		model.world?.setInventory({inventoryID: this.inventoryID, items: this.items}, this.inventoryID);
	}


}
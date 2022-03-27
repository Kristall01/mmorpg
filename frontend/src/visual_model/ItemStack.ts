import Item from "./Item";

export default class ItemStack {

	readonly item: Item
	readonly amount: number

	constructor(item: Item, amount: number) {
		this.item = item;
		this.amount = amount;
	}

}
import Item from "./Item";
import { Position } from "./VisualModel";

export default class FloatingItem {

	readonly item: Item
	readonly id: number;
	readonly pos: Position

	constructor(id: number, pos: Position, item: Item) {
		this.item = item;
		this.id = id;
		this.pos = pos;
	}

}
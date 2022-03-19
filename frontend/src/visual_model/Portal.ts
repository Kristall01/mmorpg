import { Position } from "./VisualModel";

export default class Portal {

	readonly position: Position
	readonly radius: number

	constructor(position: Position, radius: number) {
		this.position = position;
		this.radius = radius;
	}

}
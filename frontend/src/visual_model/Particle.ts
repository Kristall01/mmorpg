import { PositionFn } from "./Paths";
import { Position } from "./VisualModel";

export default class Particle {

	positionFn: PositionFn
	livesUntil: number
	color: string
	size: number

	constructor(pos: Position | PositionFn, color: string, size: number, livesUntil: number) {
		if(typeof pos === "function") {
			this.positionFn = pos;
		}
		else {
			this.positionFn = () => pos;
		}
		this.livesUntil = livesUntil;
		this.color = color;
		this.size = size;
	}

}
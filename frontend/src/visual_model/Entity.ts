import { ConstPath, EntityLinearPath, entityZigzagPath, PathFn } from "./Paths"
import { Position } from "./VisualModel"

export default class Entity {

	id: number
	type: string
	positionFn: PathFn = null!
	nick: string | null = null
	speed: number
	cachedPosition: Position = [0,0];
	cachedCanvasPosition: Position = [0,0];
	name: string | null = null;

	constructor(id: number, type: string, loc: Position, speed: number) {
		this.id = id;
		this.type = type;
		this.positionFn = ConstPath(loc);
		this.speed = speed;
	}

	calculatePosition(rendertime: number) {
		let pos = this.positionFn(rendertime);
		this.cachedPosition = pos;
	}

	getLastPosition() {
		return this.cachedPosition;
	}

	setName(name: string | null) {
		this.name = name;
	}

	walkBy(startTime: number, points: Position[]) {
		let now = performance.now();
		this.positionFn = entityZigzagPath(this.positionFn(now), startTime, points, this.speed);
	}

/* 	walk(startTime: number, from: Position, target: Position) {
		let now = performance.now();
		this.setPositionFn(EntityLinearPath(this.getLocation(now), startTime, from, target, this.speed))
	}
 */
}
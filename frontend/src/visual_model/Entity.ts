import { ConstPath, EntityLinearPath, entityZigzagPath, PathFn } from "./Paths"
import { Position } from "./VisualModel"

export default class Entity {

	id: number
	type: string
	positionFn: PathFn = null!
	nick: string | null = null
	speed: number
	cachedPosition: Position = [0,0];

	constructor(id: number, type: string, loc: Position, speed: number) {
		this.id = id;
		this.type = type;
		this.setPositionFn(ConstPath(loc))
		this.speed = speed;
	}

	getLocation(rendertime: number) {
		return this.positionFn(rendertime);
	}

	private setPositionFn(posFn :PathFn) {
		this.positionFn = (time: number) => {
			let loc = posFn(time);
			this.cachedPosition = loc;
			return loc;
		}
	}

	lastPosition() {
		return this.cachedPosition;
	}

	walkBy(startTime: number, points: Position[]) {
		let now = performance.now();
		this.setPositionFn(entityZigzagPath(this.getLocation(now), startTime, points, this.speed));
	}

/* 	walk(startTime: number, from: Position, target: Position) {
		let now = performance.now();
		this.setPositionFn(EntityLinearPath(this.getLocation(now), startTime, from, target, this.speed))
	}
 */
}
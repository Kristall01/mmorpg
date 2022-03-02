import { RenderContext } from "game/graphics/GraphicsUtils"
import WorldView, { renderConfig } from "game/graphics/worldview/WorldView"
import { EntityType } from "./EntityType"
import { ConstStatus, Direction, entityZigzagStatus, Status, StatusFn } from "./Paths"
import { Position } from "./VisualModel"

export default abstract class Entity {

	id: number
	type: EntityType
	statusFn: StatusFn = null!
	nick: string | null = null
	speed: number
	cachedStatus: Status;
	cachedCanvasPosition: Position = [0,0];
	name: string | null = null;
	private _hp: number
	maxHp: number
	alive: boolean = true

	constructor(id: number, type: EntityType, loc: Position, speed: number, facing: Direction, hp: number, maxHp: number) {
		this.id = id;
		this.type = type;
		this.statusFn = ConstStatus(loc, facing);
		this.cachedStatus = this.statusFn(performance.now());
		this.speed = speed;
		this._hp = hp;
		this.maxHp = maxHp;
	}

	calculateStatus(rendertime: number) {
		let pos = this.statusFn(rendertime);
		this.cachedStatus = pos;
	}

	setName(name: string | null) {
		this.name = name;
	}

	walkBy(startTime: number, points: Position[]) {
		let now = performance.now();
		this.statusFn = entityZigzagStatus(this.statusFn(now).position, startTime, points, this.speed);
	}

	get hp() {
		return this._hp;
	}

	set hp(amount: number) {
		if(amount > this.maxHp) {
			amount = this.maxHp;
		}
		else if(amount < 0) {
			amount = 0;
		}
		this._hp = amount;
	}

	setDead(alive: boolean) {
		this.alive = !alive;
	}

/* 	walk(startTime: number, from: Position, target: Position) {
		let now = performance.now();
		this.setPositionFn(EntityLinearPath(this.getLocation(now), startTime, from, target, this.speed))
	}
 */
}
import { ActivitySnapshot } from "./ActivityFunction"
import { EntityType } from "./EntityType"
import { ConstStatus, Direction, DirectionMode, EntityConstStatus, entityZigzagStatus, Path, Status, StatusFn } from "./Paths"
import { Position } from "./VisualModel"

export default abstract class Entity<T> {

	id: number
	type: EntityType
	path: Path = null!
	nick: string | null = null
	speed: number
	cachedStatus: Status;
	name: string | null = null;
	private _hp: number
	maxHp: number
	alive: boolean = true
	readonly directionMode: DirectionMode

	constructor(id: number, type: EntityType, loc: Position, speed: number, facing: Direction, hp: number, maxHp: number, directionMode: DirectionMode) {
		this.id = id;
		this.type = type;
		this.path = {statusFn: ConstStatus(loc, facing), positions: null};
		this.cachedStatus = this.path.statusFn(performance.now());
		this.speed = speed;
		this._hp = hp;
		this.maxHp = maxHp;
		this.directionMode = directionMode;
	}

	calculateStatus(rendertime: number) {
		let pos = this.path.statusFn(rendertime);
		this.cachedStatus = pos;
	}

	setName(name: string | null) {
		this.name = name;
	}

	walkBy(startTime: number, points: Position[]) {
		let now = performance.now();
		this.path = {
			statusFn: entityZigzagStatus(this.directionMode, this.path.statusFn(now).position, startTime, points, this.speed),
			positions: points
		};
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

	abstract attack(pos: Position): void;

	abstract activity(rendertime: number): ActivitySnapshot<T>;

	teleport(pos: Position, instant: boolean) {
		let now = performance.now();
		if(instant) {
			this.path = {
				statusFn: ConstStatus(pos, this.path.statusFn(now).facing),
				positions: null
			}
		}
		else {
			let status = this.path.statusFn(now)
			this.path = {
				statusFn: EntityConstStatus(this.directionMode, status.position, pos, status.facing),
				positions: null
			}

		}
	}

/* 	walk(startTime: number, from: Position, target: Position) {
		let now = performance.now();
		this.setPositionFn(EntityLinearPath(this.getLocation(now), startTime, from, target, this.speed))
	}
 */
}
import ActivityFunction, { ActivitySnapshot, createSwordFunction, createWalkFunction, idleFunction } from "visual_model/ActivityFunction";
import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { calculatedDirection, Direction, facingFunction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";

export default class SkeletonEntity extends Entity {

	private activityFn: ActivityFunction = idleFunction;
	
	constructor(id: number, loc: Position, speed: number, hp: number, maxHp: number) {
		super(id, EntityType.enum.map.SKELETON, loc, speed, Direction.enum.map.SOUTH, hp, maxHp, "4");
	}

	attack(pos: Position): void {
		let currentPos = this.cachedStatus.position;
		this.statusFn = facingFunction(calculatedDirection(this.directionMode, pos[0]-currentPos[0], pos[1]-currentPos[1]), this.statusFn);
		this.activityFn = createSwordFunction();
	}

	walkBy(startTime: number, points: Position[]): void {
		if(this.cachedStatus.moving) {
			this.activityFn = createWalkFunction(() => this.cachedStatus, startTime);
		}
		else {
			this.activityFn = createWalkFunction(() => this.cachedStatus, 0)
		}
		super.walkBy(startTime, points);
	}	

	activity(rendertime: number): ActivitySnapshot {
		return this.activityFn(rendertime);
	}

}
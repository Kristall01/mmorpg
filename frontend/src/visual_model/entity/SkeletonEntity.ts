import ActivityFunction, { ActivitySnapshot, createIdleFunction, createSwordFunction, createWalkFunction } from "visual_model/ActivityFunction";
import SkeletonActivity from "visual_model/assetconfig/SkeletonAssetConfig";
import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { calculatedDirection, Direction, facingFunction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";

export default class SkeletonEntity extends Entity<SkeletonActivity> {

	private activityFn: ActivityFunction<SkeletonActivity> = createIdleFunction<SkeletonActivity>(SkeletonActivity.enum.map);
	
	constructor(id: number, loc: Position, speed: number, hp: number, maxHp: number) {
		super(id, EntityType.enum.map.SKELETON, loc, speed, Direction.enum.map.SOUTH, hp, maxHp, "4");
	}

	attack(pos: Position): void {
		let currentPos = this.cachedStatus.position;
		this.statusFn = facingFunction(calculatedDirection(this.directionMode, pos[0]-currentPos[0], pos[1]-currentPos[1]), this.statusFn);
		this.activityFn = createSwordFunction<SkeletonActivity>(SkeletonActivity.enum.map);
	}

	walkBy(startTime: number, points: Position[]): void {
		if(this.cachedStatus.moving) {
			this.activityFn = createWalkFunction<SkeletonActivity>(SkeletonActivity.enum.map, () => this.cachedStatus, startTime);
		}
		else {
			this.activityFn = createWalkFunction<SkeletonActivity>(SkeletonActivity.enum.map, () => this.cachedStatus, 0)
		}
		super.walkBy(startTime, points);
	}	

	activity(rendertime: number): ActivitySnapshot<SkeletonActivity> {
		return this.activityFn(rendertime);
	}

}
import ActivityFunction, { ActivitySnapshot, createIdleFunction, createSwordFunction, createWalkFunction } from "visual_model/ActivityFunction";
import OgreActivity from "visual_model/assetconfig/OgreAssetConfig";
import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { calculatedDirection, Direction, facingFunction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";

export default class OgreEntity extends Entity<OgreActivity> {

	private activityFn: ActivityFunction<OgreActivity> = createIdleFunction<OgreActivity>(OgreActivity.enum.map);
	
	constructor(id: number, loc: Position, speed: number, hp: number, maxHp: number) {
		super(id, EntityType.enum.map.OGRE, loc, speed, Direction.enum.map.SOUTH, hp, maxHp, "4");
	}

	attack(pos: Position): void {
		let currentPos = this.cachedStatus.position;
		this.statusFn = facingFunction(calculatedDirection(this.directionMode, pos[0]-currentPos[0], pos[1]-currentPos[1]), this.statusFn);
		this.activityFn = createSwordFunction<OgreActivity>(OgreActivity.enum.map);
	}

	walkBy(startTime: number, points: Position[]): void {
		if(this.cachedStatus.moving) {
			this.activityFn = createWalkFunction<OgreActivity>(OgreActivity.enum.map, () => this.cachedStatus, startTime);
		}
		else {
			this.activityFn = createWalkFunction<OgreActivity>(OgreActivity.enum.map, () => this.cachedStatus, 0)
		}
		super.walkBy(startTime, points);
	}	

	activity(rendertime: number): ActivitySnapshot<OgreActivity> {
		return this.activityFn(rendertime);
	}

}
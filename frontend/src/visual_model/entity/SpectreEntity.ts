import ActivityFunction, { ActivitySnapshot, createIdleFunction, createSwordFunction, createWalkFunction } from "visual_model/ActivityFunction";
import SpectreActivity from "visual_model/assetconfig/SpectreAssetConfig";
import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { calculatedDirection, Direction, facingFunction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";

export default class SpectreEntity extends Entity<SpectreActivity> {

	private activityFn: ActivityFunction<SpectreActivity> = createIdleFunction<SpectreActivity>(SpectreActivity.enum.map);
	
	constructor(id: number, loc: Position, speed: number, hp: number, maxHp: number) {
		super(id, EntityType.enum.map.SPECTRE, loc, speed, Direction.enum.map.SOUTH, hp, maxHp, "4");
	}

	attack(pos: Position): void {
		let currentPos = this.cachedStatus.position;
		this.path = {
			statusFn: facingFunction(calculatedDirection(this.directionMode, pos[0]-currentPos[0], pos[1]-currentPos[1]), this.path.statusFn),
			positions: null
		}
		this.activityFn = createSwordFunction<SpectreActivity>(SpectreActivity.enum.map);
	}

	walkBy(startTime: number, points: Position[]): void {
		if(this.cachedStatus.moving) {
			this.activityFn = createWalkFunction<SpectreActivity>(SpectreActivity.enum.map, () => this.cachedStatus, startTime);
		}
		else {
			this.activityFn = createWalkFunction<SpectreActivity>(SpectreActivity.enum.map, () => this.cachedStatus, 0)
		}
		super.walkBy(startTime, points);
	}	

	activity(rendertime: number): ActivitySnapshot<SpectreActivity> {
		return this.activityFn(rendertime);
	}

}
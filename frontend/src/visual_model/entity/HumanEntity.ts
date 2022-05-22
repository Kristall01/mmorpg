import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { HumanActivity, Skintone } from "visual_model/assetconfig/HumanAssetConfig";
import { Position } from "visual_model/VisualModel";
import { calculatedDirection, Direction, facingFunction } from "visual_model/Paths";
import { ColoredCloth } from "game/graphics/renderers/world/HumanRenderer";
import ActivityFunction, { ActivitySnapshot, createIdleFunction, createSwordFunction, createWalkFunction } from "visual_model/ActivityFunction";

export default class HumanEntity extends Entity<HumanActivity> {

	skin: Skintone = 0;

	private activityFn: ActivityFunction<HumanActivity> = createIdleFunction(HumanActivity.enum.map);
	
	clothes: ColoredCloth[] = [
/* 		Cloth.enum.map.,
		Cloth.enum.map.PANTS_SUIT,
		Cloth.enum.map.SHOES,
 */	];

	constructor(id: number, loc: Position, speed: number, facing: Direction, hp: number, maxHp: number) {
		super(id, EntityType.enum.map.HUMAN, loc, speed, facing, hp, maxHp, "4");
	}

	attack(pos: Position): void {
		let currentPos = this.cachedStatus.position;
		this.statusFn = facingFunction(calculatedDirection(this.directionMode, pos[0]-currentPos[0], pos[1]-currentPos[1]), this.statusFn);
		this.activityFn = createSwordFunction<HumanActivity>(HumanActivity.enum.map);
	}

	walkBy(startTime: number, points: Position[]): void {
		if(this.cachedStatus.moving) {
			this.activityFn = createWalkFunction(HumanActivity.enum.map, () => this.cachedStatus, startTime);
		}
		else {
			this.activityFn = createWalkFunction(HumanActivity.enum.map, () => this.cachedStatus, 0)
		}
		super.walkBy(startTime, points);
	}

	changeClothes(clothes: ColoredCloth[]) {
		this.clothes = clothes;
	}

	activity(rendertime: number): ActivitySnapshot<HumanActivity> {
		return this.activityFn(rendertime);
	}

}
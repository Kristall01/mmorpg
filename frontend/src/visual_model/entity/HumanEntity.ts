import CozyPack from "game/graphics/texture/CozyPack";
import { RenderContext } from "game/graphics/GraphicsUtils";
import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { Activity, Cloth, ClothColor, Skintone } from "visual_model/human/HumanAssetConfig";
import { Position } from "visual_model/VisualModel";
import { calculatedDirection, Direction, facingFunction } from "visual_model/Paths";
import { enumValueOf } from "utils";
import { ColoredCloth } from "game/graphics/renderers/world/HumanRenderer";
import { ColoredClothData } from "model/signals/SignalChangeClothes";
import ActivityFunction, { ActivitySnapshot, createSwordFunction, createWalkFunction, idleFunction } from "visual_model/ActivityFunction";

export default class HumanEntity extends Entity {

	skin: Skintone = 0;

	private activityFn: ActivityFunction = idleFunction;
	
	clothes: ColoredCloth[] = [
/* 		Cloth.enum.map.,
		Cloth.enum.map.PANTS_SUIT,
		Cloth.enum.map.SHOES,
 */	];

	constructor(id: number, loc: Position, speed: number, facing: Direction, hp: number, maxHp: number) {
		super(id, EntityType.enum.map.HUMAN, loc, speed, facing, hp, maxHp);
	}

	attack(pos: Position): void {
		let currentPos = this.cachedStatus.position;
		this.statusFn = facingFunction(calculatedDirection(pos[0]-currentPos[0], pos[1]-currentPos[1]), this.statusFn);
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

	changeClothes(clothes: ColoredCloth[]) {
		this.clothes = clothes;
	}

	activity(rendertime: number): ActivitySnapshot {
		return this.activityFn(rendertime);
	}

}
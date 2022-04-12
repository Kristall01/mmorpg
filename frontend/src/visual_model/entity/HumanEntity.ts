import CozyPack from "game/graphics/texture/CozyPack";
import { RenderContext } from "game/graphics/GraphicsUtils";
import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { Activity, Cloth, ClothColor, Skintone } from "visual_model/human/HumanAssetConfig";
import { Position } from "visual_model/VisualModel";
import { Direction } from "visual_model/Paths";
import { enumValueOf } from "utils";
import { ColoredCloth } from "game/graphics/renderers/world/HumanRenderer";
import { ColoredClothData } from "model/signals/SignalChangeClothes";

export default class HumanEntity extends Entity {

	skin: Skintone = 0;
	activity: Activity = Activity.enum.map.WALK;
	activityStart: number = 0
	
	clothes: ColoredCloth[] = [
/* 		Cloth.enum.map.,
		Cloth.enum.map.PANTS_SUIT,
		Cloth.enum.map.SHOES,
 */	];

	constructor(id: number, loc: Position, speed: number, facing: Direction, hp: number, maxHp: number) {
		super(id, EntityType.enum.map.HUMAN, loc, speed, facing, hp, maxHp);
	}

	walkBy(startTime: number, points: Position[]): void {
		if(!this.cachedStatus.moving) {
			this.activityStart = startTime;
		}
		super.walkBy(startTime, points);
	}

	changeClothes(clothes: ColoredCloth[]) {
		this.clothes = clothes;
	}

}
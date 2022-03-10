import CozyPack from "game/graphics/texture/CozyPack";
import { RenderContext } from "game/graphics/GraphicsUtils";
import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { Activity, Cloth, Skintone } from "visual_model/human/HumanAssetConfig";
import { Position } from "visual_model/VisualModel";
import { Direction } from "visual_model/Paths";
import { enumValueOf } from "utils";

export default class HumanEntity extends Entity {

	skin: Skintone = 0;
	activity: Activity = Activity.enum.map.WALK;
	activityStart: number = 0
	
	clothes: Cloth[] = [
/* 		Cloth.enum.map.,
		Cloth.enum.map.PANTS_SUIT,
		Cloth.enum.map.SHOES,
 */	];

	constructor(id: number, loc: Position, speed: number, facing: Direction) {
		super(id, EntityType.enum.map.HUMAN, loc, speed, facing);
	}

	walkBy(startTime: number, points: Position[]): void {
		if(!this.cachedStatus.moving) {
			this.activityStart = startTime;
		}
		super.walkBy(startTime, points);
	}

	changeClothes(clothesNames: string[]) {
		let clothes: Cloth[] = [];
		for(let clothName of clothesNames) {
			let cloth = enumValueOf(Cloth.enum.map, clothName);
			if(cloth !== null) {
				clothes.push(cloth);
			}
		}
		this.clothes = clothes;
	}

}
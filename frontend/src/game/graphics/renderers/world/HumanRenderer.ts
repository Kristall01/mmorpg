import { RenderContext } from "game/graphics/GraphicsUtils";
import { CozyActivity } from "game/graphics/texture/CozyPack";
import { Activity, Cloth, ClothColor, Skintone } from "visual_model/human/HumanAssetConfig";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";

export interface ColoredCloth {
	color: ClothColor,
	cloth: Cloth
}

export const renderHuman = (ctx: RenderContext, skin: Skintone, facing: Direction, activity: CozyActivity, animationTime: number, renderPos: Position, scale: number, clothes: Array<ColoredCloth | null>) => {
	activity.human(skin).drawTo(ctx, facing, renderPos, scale, animationTime);
	for(let e of clothes) {
		if(e === null) {
			continue;
		}
		let {cloth, color} = e;
		activity.getCozyCloth(cloth).ofColor(color).drawTo(ctx, facing, renderPos, scale, animationTime);
	}
}

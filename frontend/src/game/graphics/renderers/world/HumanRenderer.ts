import { RenderContext } from "game/graphics/GraphicsUtils";
import CozyPack, { CozyActivity } from "game/graphics/texture/CozyPack";
import { Cloth, ClothColor, HumanActivity, Skintone } from "visual_model/assetconfig/HumanAssetConfig";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";
import EntityRenderer, { EntityLike } from "./entities/EntityRenderer";

export interface ColoredCloth {
	color: ClothColor,
	cloth: Cloth
}

export default class HumanRenderer extends EntityRenderer {

	private cozyPack: CozyPack

	constructor(pack: CozyPack) {
		super();
		this.cozyPack = pack;
	}

	drawTo(ctx: RenderContext, skin: Skintone, facing: Direction, activity: HumanActivity, animationTime: number, renderPos: Position, scale: number, clothes: Array<ColoredCloth | null>, e?: EntityLike) {
		let cozyActivity: CozyActivity = this.cozyPack.getCozyActivity(activity);
		this.drawToCozy(ctx, skin, facing, cozyActivity, animationTime, renderPos, scale, clothes, e);
	}

	drawToCozy(ctx: RenderContext, skin: Skintone, facing: Direction, cozyActivity: CozyActivity, animationTime: number, renderPos: Position, scale: number, clothes: Array<ColoredCloth | null>, e?: EntityLike) {
		if(e !== undefined) {
			super.renderEntity(ctx, e, renderPos, scale);
		}
		const centeredRender = true;
	
		cozyActivity.human(skin).drawTo(ctx, centeredRender, facing, renderPos, scale, animationTime);
		for(let e of clothes) {
			if(e === null) {
				continue;
			}
			let {cloth, color} = e;
			cozyActivity.getCozyCloth(cloth).ofColor(color).drawTo(ctx, centeredRender, facing, renderPos, scale, animationTime);
		}
	}
	

}

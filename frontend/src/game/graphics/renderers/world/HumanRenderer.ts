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

export interface HumanRenderOptions {
	entity?: EntityLike,
	translate?: [number,number] | "center"
}

export default class HumanRenderer extends EntityRenderer {

	private cozyPack: CozyPack

	constructor(pack: CozyPack) {
		super();
		this.cozyPack = pack;
	}

	drawTo(ctx: RenderContext, skin: Skintone, facing: Direction, activity: HumanActivity, animationTime: number, renderPos: Position, scale: number, clothes: Array<ColoredCloth | null>, opt?: HumanRenderOptions) {
		let cozyActivity: CozyActivity = this.cozyPack.getCozyActivity(activity);
		this.drawToCozy(ctx, skin, facing, cozyActivity, animationTime, renderPos, scale, clothes, opt);
	}

	drawToCozy(ctx: RenderContext, skin: Skintone, facing: Direction, cozyActivity: CozyActivity, animationTime: number, renderPos: Position, scale: number, clothes: Array<ColoredCloth | null>, opt?: HumanRenderOptions) {
		if(opt !== undefined && opt.entity !== undefined) {
			super.renderEntity(ctx, opt.entity, renderPos, scale);
		}
	
		cozyActivity.human(skin).drawTo(ctx, facing, renderPos, scale, animationTime, opt?.translate);
		for(let e of clothes) {
			if(e === null) {
				continue;
			}
			let {cloth, color} = e;
			cozyActivity.getCozyCloth(cloth).ofColor(color).drawTo(ctx, facing, renderPos, scale, animationTime, opt?.translate);
		}
	}
	

}

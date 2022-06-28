import { drawBar, drawText, RenderContext } from "game/graphics/GraphicsUtils";
import { ParsedText } from "game/ui/chat/textparser";
import VisualResources from "game/VisualResources";
import Entity from "visual_model/Entity";
import HumanEntity from "visual_model/entity/HumanEntity";
import EntityType from "visual_model/EntityType";
import { Position } from "visual_model/VisualModel";
import WorldRenderer, { RenderConfig } from "../WorldRenderer";

type a = {
	fn: (world: WorldRenderer, renderConfig: RenderConfig) => void,
	magnify: number
}

export const createRendererFor = (e: Entity<any>, visuals: VisualResources): a => {
	const entityTypes = EntityType.enum.map;
	switch(e.type) {
		case entityTypes.SKELETON: {
			const magnify = 3;
			return {
				fn: (world: WorldRenderer, config: RenderConfig) => {
					let activity = e.activity(config.rendertime);
					let cachedStatus = e.cachedStatus;
					visuals.skeletonRenderer.drawTo(world.ctx, cachedStatus.facing, world.translateXY(...cachedStatus.position), e, config.tileSize*magnify, activity.animationTime, activity.activity);
				},
				magnify
			}
		}
		case entityTypes.OGRE: {
			const magnify = 3;
			return {
				fn: (world: WorldRenderer, config: RenderConfig) => {
					let activity = e.activity(config.rendertime);
					let cachedStatus = e.cachedStatus;
					visuals.ogreRenderer.drawTo(world.ctx, cachedStatus.facing, world.translateXY(...cachedStatus.position), e, config.tileSize*magnify, activity.animationTime, activity.activity);
				},
				magnify
			}
		}
		case entityTypes.SLIME: {
			const magnify = 1
			return {
				fn: (world: WorldRenderer, config: RenderConfig) => {
					let activity = e.activity(config.rendertime);
					let cachedStatus = e.cachedStatus;
					visuals.slimeRenderer.drawTo(world.ctx, cachedStatus.facing, world.translateXY(...cachedStatus.position), e, config.tileSize*magnify, activity.animationTime);
				},
				magnify
			}
		}
		case entityTypes.HUMAN: {
			const magnify = 2
			return {
				fn: (world: WorldRenderer, config: RenderConfig) => {
					let human = e as HumanEntity;
					let cachedStatus = e.cachedStatus;
					let translated = world.translateXY(...cachedStatus.position);
					let {activity, animationTime} = human.activity(config.rendertime);
					visuals.humanRenderer.drawTo(world.ctx, human.skin, cachedStatus.facing, activity, animationTime, translated, config.tileSize*magnify, human.clothes, e);
				},
				magnify
			}
			
/*			let cozyActivity = world.visuals.cozy.getCozyActivity(activity);
			cozyActivity.human(human.skin).drawTo(world.ctx, true, cachedStatus.facing, translated, config.tileSize*2, animationTime);
			for(let clothes of human.clothes) {
				cozyActivity.getCozyCloth(clothes.cloth).ofColor(clothes.color).drawTo(world.ctx, true, cachedStatus.facing, translated, config.tileSize*2, animationTime);
			} */
		}
		case entityTypes.DUMMY: {
			const magnify = 1;
			return {
				fn: (world: WorldRenderer, config: RenderConfig) => {
					visuals.unknownEntityRenderer.renderEntity(world.ctx, e, world.translateXY(...e.cachedStatus.position), config.tileSize*magnify);
				},
				magnify
			}
			
/* 			let cozyActivity = world.visuals.cozy.getCozyActivity(activity);
			cozyActivity.human(human.skin).drawTo(world.ctx, true, cachedStatus.facing, translated, config.tileSize*2, animationTime);
			for(let clothes of human.clothes) {
				cozyActivity.getCozyCloth(clothes.cloth).ofColor(clothes.color).drawTo(world.ctx, true, cachedStatus.facing, translated, config.tileSize*2, animationTime);
			} */
		}
		case entityTypes.SPECTRE: {
			const magnify = 2;
			return {
				fn: (world: WorldRenderer, config: RenderConfig) => {
					let activity = e.activity(config.rendertime);
					let cachedStatus = e.cachedStatus;
					visuals.spectreRenderer.drawTo(world.ctx, cachedStatus.facing, world.translateXY(...cachedStatus.position), e, config.tileSize*magnify, activity.animationTime, activity.activity);
				},
				magnify
			}
		}
		default: {
			const magnify = 1;
			return {
				fn: (world: WorldRenderer, config: RenderConfig) => {
					let cachedStatus = e.cachedStatus;
					visuals.unknownEntityRenderer.renderEntity(world.ctx, e, world.translateXY(...cachedStatus.position), config.tileSize*magnify);
				},
				magnify
			}
		}
	}
}

export interface EntityLike {
	hp: number,
	maxHp: number
	alive: boolean
	type: EntityType
	nameData: Array<ParsedText> | null
}

export default class EntityRenderer {

	renderEntity(ctx: RenderContext, e: EntityLike, pos: Position, tileSize: number) {

		pos[0] = Math.floor(pos[0]);
		pos[1] = Math.floor(pos[1]);
	
		let eHeight = e.type.height*1.25 * tileSize;
		let top = pos[1] - eHeight/2;
	
		if(e.alive) {
			let hpPercent = e.hp / e.maxHp;
			let rgb = "?";
			if(hpPercent < 0.5) {
				rgb = `rgb(255, ${Math.round(hpPercent*512)},0)`;
			}
			else {
				rgb = `rgb(${(Math.round((1 - hpPercent)*512))},255,0)`;
			}
			let [barWidth, barHeight] = drawBar(ctx, [pos[0], top], (e.hp / e.maxHp), {fillColor: rgb});
			top -= barHeight+5;
		}
	
		let nameData = e.nameData;
		if(nameData !== null) {
			for(let i = nameData.length - 1; i >= 0; --i) {
				top -= (drawText(ctx, [pos[0], top], nameData[i], "end", "middle")[1]);
			}
			top -= 5;
		}
	}

}
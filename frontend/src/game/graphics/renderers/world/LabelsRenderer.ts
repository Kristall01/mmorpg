import { drawDamageLabel, drawHealLabel } from "game/graphics/GraphicsUtils";
import WorldView from "game/graphics/world/WorldView";
import { LabelType, WorldLabel } from "visual_model/Label";
import { Position } from "visual_model/VisualModel";
import World from "visual_model/World";
import WorldRenderer, { RenderConfig } from "./WorldRenderer";

const animationTime = 750;

export const renderLabels = (view: WorldRenderer, world: World, config: RenderConfig) => {
	world.filterLabels(l => (l.startTime + animationTime > config.rendertime));
	for(let label of world.labels()) {
		renderLabel(view, label, config);
	}
}

export const renderLabel = (view: WorldRenderer, label: WorldLabel, config: RenderConfig) => {
	let t = config.rendertime;
	let pos = label.entity.cachedCanvasPosition;
	let eHeight = label.entity.type.height*1.25 * config.tileSize;
	let top = pos[1] - eHeight;
	let drawPos: Position = [pos[0], top+(eHeight*0.5)];
	let drawProgress = ((config.rendertime - label.startTime))/animationTime
	switch(label.type) {
		case LabelType.enum.map.DAMAGE: {
			drawDamageLabel(view.ctx, drawPos, drawProgress, label.text);
			break;
		}
		case LabelType.enum.map.HEAL: {
			drawHealLabel(view.ctx, drawPos, drawProgress, label.text);
			break
		}
	}
	//drawDamageLabel(view.ctx, pos, (t - label.startTime) / 1000, label.text);
}

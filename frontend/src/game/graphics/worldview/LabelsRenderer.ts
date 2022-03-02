import { LabelType, WorldLabel } from "visual_model/Label";
import { Position } from "visual_model/VisualModel";
import World from "visual_model/World";
import { drawDamageLabel, drawHealLabel } from "../GraphicsUtils";
import WorldView, { renderConfig } from "./WorldView";

const animationTime = 750;

export const renderLabels = (view: WorldView, world: World, config: renderConfig) => {
	world.filterLabels(l => (l.startTime + animationTime > config.rendertime));
	for(let label of world.labels()) {
		renderLabel(view, label, config);
	}
}

export const renderLabel = (view: WorldView, label: WorldLabel, config: renderConfig) => {
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

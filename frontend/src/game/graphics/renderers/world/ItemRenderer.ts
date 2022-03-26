import { drawText, RenderContext } from "game/graphics/GraphicsUtils";
import FloatingItem from "visual_model/FloatingItem";
import WorldRenderer, { RenderConfig } from "./WorldRenderer";

export const renderItem = (ctx: RenderContext, world: WorldRenderer, item: FloatingItem) => {
	let pos = item.pos;
	let canvasPos = world.translateXY(...pos);
	drawText(ctx, canvasPos, item.item.name);
}

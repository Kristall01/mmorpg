import { RenderContext } from "game/graphics/GraphicsUtils";
import { Position } from "visual_model/VisualModel";

export default interface Texture {

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void;

}
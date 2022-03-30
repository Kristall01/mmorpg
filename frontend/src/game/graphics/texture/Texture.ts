import { RenderContext } from "game/graphics/GraphicsUtils";
import { Position } from "visual_model/VisualModel";
import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import StaticSpriteTexture from "./StaticSpriteTexture";
import StaticTexture from "./StaticTexture";

export default abstract class Texture {

	readonly id: string;

	constructor(id: string) {
		this.id = id;
	}

	abstract drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number, otherSize?: number, translateX?: number, translateY?: number): void;

}
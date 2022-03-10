import { RenderContext } from "game/graphics/GraphicsUtils";
import { Position } from "visual_model/VisualModel";
import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import StaticTexture from "./StaticTexture";

export default interface Texture {

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void;

}

export function ofType(type: string, img: HTMLImageElement, others: any) {
	switch(type) {
		case "static_global": {
			return new StaticTexture(img);
		}
		case "animated_global_column": {
			return new AnimatedGlobalColumnTexture(img, others.sliceTime);
		}
		default: {
			throw new Error("unknown texture type "+JSON.stringify(type));
		}
	}
}
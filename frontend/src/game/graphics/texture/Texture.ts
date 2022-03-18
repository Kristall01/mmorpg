import { RenderContext } from "game/graphics/GraphicsUtils";
import { Position } from "visual_model/VisualModel";
import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import StaticTexture from "./StaticTexture";

export default abstract class Texture {

	readonly id: string;

	constructor(id: string) {
		this.id = id;
	}

	abstract drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void;

}

export function ofType(id: string, type: string, img: HTMLImageElement, others: any) {
	switch(type) {
		case "static_global": {
			return new StaticTexture(id, img);
		}
		case "animated_global_column": {
			return new AnimatedGlobalColumnTexture(id, img, others.sliceTime);
		}
		default: {
			throw new Error("unknown texture type "+JSON.stringify(type));
		}
	}
}
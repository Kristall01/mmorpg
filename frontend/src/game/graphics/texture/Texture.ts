import { RenderContext } from "game/graphics/GraphicsUtils";
import { Position } from "visual_model/VisualModel";
import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import StaticSpriteTexture from "./StaticSpriteTexture";
import StaticTexture from "./StaticTexture";

export default interface Texture {

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number, otherSize?: number, translateX?: number, translateY?: number): void;

}

export function ofType(type: string, img: HTMLImageElement, others: any): Texture | Map<string, Texture> {
	switch(type) {
		case "static_global": {
			return new StaticTexture(img);
		}
		case "animated_global_column": {
			return new AnimatedGlobalColumnTexture(img, others.sliceTime);
		}
		case "static_sprite": {
			return new StaticSpriteTexture(img, others.cell_size, others.x, others.y);
		}
		case "indexed_sprite": {
			let {tile_width, tile_height} = others;
			if(tile_width === undefined || tile_height === undefined) {
				throw new Error("indexed_sprite requires tile_width and tile_height");
			}
			if(img.width % tile_width !== 0) {
				throw new Error("indexed_sprite requires image width to be divisible by tile_width");
			}
			if(img.height % tile_height !== 0) {
				throw new Error("indexed_sprite requires image height to be divisible by tile_height");
			}
			let i = 0;
			let obj = new Map<string, Texture>();
			const cols = img.width / tile_width;
			const rows = img.height / tile_height;
			for(let y = 0; y < rows; ++y) {
				for(let x = 0; x < cols; ++x) {
					obj.set((i++).toString(), new StaticSpriteTexture(img, tile_width, x, y));
				}
			}
			return obj;
		}
		default: {
			throw new Error("unknown texture type "+JSON.stringify(type));
		}
	}
}
import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import StaticSpriteTexture from "./StaticSpriteTexture";
import StaticTexture from "./StaticTexture";

export default function createTexture(id: string, type: string, img: HTMLImageElement, others: any) {
	switch(type) {
		case "static_global": {
			return new StaticTexture(id, img);
		}
		case "animated_global_column": {
			return new AnimatedGlobalColumnTexture(id, img, others.sliceTime);
		}
		case "static_sprite": {
			return new StaticSpriteTexture(id, img, others.cell_size, others.x, others.y);
		}
		default: {
			throw new Error("unknown texture type "+JSON.stringify(type));
		}
	}
}
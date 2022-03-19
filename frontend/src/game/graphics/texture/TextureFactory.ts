import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import StaticTexture from "./StaticTexture";

export default function createTexture(id: string, type: string, img: HTMLImageElement, others: any) {
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
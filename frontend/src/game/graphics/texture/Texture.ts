import { RenderContext } from "game/graphics/GraphicsUtils";
import { Position } from "visual_model/VisualModel";
import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import StaticSpriteTexture from "./StaticSpriteTexture";
import StaticTexture from "./StaticTexture";

export default abstract class Texture {

	readonly id: string;
	readonly baseWidth: number;
	readonly baseHeight: number;
	private snapshot: HTMLImageElement | null = null

	constructor(id: string, baseWidth: number, baseHeight: number) {
		this.id = id;
		this.baseWidth = baseWidth;
		this.baseHeight = baseHeight;
	}

	getSnapshot() {
		if(this.snapshot !== null) {
			return this.snapshot;
		}
		let c = document.createElement("canvas");
		c.width = this.baseWidth;
		c.height = this.baseHeight;
		let ctx: CanvasRenderingContext2D = c.getContext("2d")!;
		ctx.imageSmoothingEnabled = false;
		this.drawTo(0, ctx, [0,0], this.baseWidth, this.baseHeight);
		let imgSrc = c.toDataURL("image/png",1);
		let imgElement = new Image(this.baseWidth, this.baseHeight);
		imgElement.src = imgSrc;
		this.snapshot = imgElement;
		return imgElement;
	}

	abstract drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number, otherSize?: number, translateX?: number, translateY?: number): void;

}
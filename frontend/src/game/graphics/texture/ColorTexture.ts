import { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import Texture from "./Texture";

export default class ColorTexture implements Texture {

	private rgb: string

	constructor(rgb: string) {
		this.rgb = rgb;
	}
	image(): HTMLImageElement | null {
		return null;
	}
	drawTo(rendertime: number, ctx: RenderContext, [x,y]: Position, size: number): void {
		ctx.fillStyle = this.rgb;
		ctx.fillRect(x,y,size,size);
	}
	
}
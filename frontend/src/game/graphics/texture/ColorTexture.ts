import { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import Texture from "./Texture";

export default class ColorTexture extends Texture {

	private rgb: string

	constructor(id: string, rgb: string) {
		super(id, 1, 1);
		this.rgb = rgb;
	}

	drawTo(rendertime: number, ctx: RenderContext, [x,y]: Position, size: number): void {
		ctx.fillStyle = this.rgb;
		ctx.fillRect(x,y,size,size);
	}
	
}
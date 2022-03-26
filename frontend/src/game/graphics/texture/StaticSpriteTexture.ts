import { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import Texture from "./Texture";

export default class StaticSpriteTexture implements Texture {

	private img: HTMLImageElement
	private startX: number
	private startY: number
	private size: number

	constructor(img: HTMLImageElement, scale: number, x: number, y: number) {
		this.img = img;
		this.startX = x*scale;
		this.startY = y*scale;
		this.size = scale;
	}

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void {
		ctx.drawImage(this.img, this.startX, this.startY, this.size, this.size, position[0], position[1], size, size);
	}

}
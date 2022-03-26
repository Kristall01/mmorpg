import { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import Texture from "./Texture";

export default class StaticSpriteTexture implements Texture {

	private img: HTMLImageElement
	private startX: number
	private startY: number
	private size: number
	private translateX: number
	private translateY: number

	constructor(img: HTMLImageElement, scale: number, x: number, y: number, translateX?: number, translateY?: number) {
		this.img = img;
		this.startX = x*scale;
		this.startY = y*scale;
		this.size = scale;
		this.translateX = translateX ?? 0;
		this.translateY = translateY ?? 0;
	}

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void {
		ctx.drawImage(this.img, this.startX, this.startY, this.size, this.size, position[0] + this.translateX*size, position[1] + this.translateY*size, size, size);
	}

}
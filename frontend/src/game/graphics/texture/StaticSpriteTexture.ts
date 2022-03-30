import { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import Texture from "./Texture";

export default class StaticSpriteTexture extends Texture {

	private img: HTMLImageElement
	private startX: number
	private startY: number
	private size: number

	readonly id: string;

	constructor(id: string, img: HTMLImageElement, scale: number, x: number, y: number) {
		super(id, scale, scale);
		this.id = id;
		this.img = img;
		this.startX = x*scale;
		this.startY = y*scale;
		this.size = scale;
	}

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number, otherSize?: number, translateX: number = 0, translateY: number = 0): void {
		ctx.drawImage(this.img, this.startX, this.startY, this.size, this.size, position[0] + translateX*size, position[1] + translateY*size, size, otherSize ?? size);
	}

}
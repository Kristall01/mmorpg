import { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import Texture from "./Texture";

export default class AnimatedGlobalColumnTexture extends Texture {

	private img: HTMLImageElement
	private width: number
	private sliceHeight: number
	private timeFrame: number
	private sliceTime: number

	constructor(id: string, img: HTMLImageElement, sliceTime: number, slices?: number) {
		super(id, img.width, slices === undefined ? img.width : img.height/slices);
		this.img = img;
		this.width = img.width;

		if(slices === undefined) {
			slices = img.height / img.width;
		}

		this.timeFrame = sliceTime*slices;

		this.sliceHeight = img.height / slices;
		this.sliceTime = this.timeFrame / slices;
	}

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void {


		let currentTime = rendertime % this.timeFrame; // [0, 2000[
		let i = currentTime / this.sliceTime // [0, slices]
		let index = Math.floor(i);

		ctx.drawImage(this.img, 0, index*this.sliceHeight, this.width, this.sliceHeight, position[0], position[1], size, size);
	}

}
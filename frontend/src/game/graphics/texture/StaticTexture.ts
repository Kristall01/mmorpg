import { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import Texture from "./Texture";

export default class StaticTexture extends Texture {

	private img: HTMLImageElement
	private width: number
	private height: number

	constructor(id: string, img: HTMLImageElement) {
		super(id);
		this.img = img;
		this.width = img.width;
		this.height = img.height;
	}

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void {
		ctx.drawImage(this.img, 0, 0, this.width, this.height, position[0], position[1], size, size);
	}

}

/**

import { Position } from "visual_model/VisualModel";
import { RenderContext, resizeImage } from "../GraphicsUtils";
import Texture from "./Texture";

export default class StaticTexture implements Texture {

	private img: HTMLImageElement
	private width: number
	private height: number
	private resizeMap: Map<number, ImageData>

	constructor(img: HTMLImageElement) {
		this.img = img;
		this.width = img.width;
		this.height = img.height;
		this.resizeMap = new Map();
	}

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void {
		let imgdata = this.resizeMap.get(size);
		if(imgdata === undefined) {
			imgdata = resizeImage(this.img, size, size);
			this.resizeMap.set(size, imgdata);
		}
		ctx.putImageData(imgdata, position[0], position[1]);
		//ctx.drawImage(this.img, 0, 0, this.width, this.height, position[0], position[1], size, size);
	}

}

 */
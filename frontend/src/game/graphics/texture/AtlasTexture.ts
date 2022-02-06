import { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import Texture from "./Texture";

export default class AtlasTexture implements Texture {

	private img: HTMLImageElement
	private points: Array<{start: Position, width: number, height: number}>
	private timeMod: number

	constructor(img: HTMLImageElement, points: Array<{start: Position, width: number, height: number}>) {
		this.img = img;

		this.points = points;
		this.timeMod = 1000/points.length;
	}

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void {
		let i = Math.floor((rendertime % 1000)/this.timeMod);
		let point = this.points[i];
		ctx.drawImage(this.img, point.start[0], point.start[1], point.width, point.height, position[0], position[1], size, size);
	}

}
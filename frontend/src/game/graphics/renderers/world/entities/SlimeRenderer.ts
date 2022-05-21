import { RenderContext } from "game/graphics/GraphicsUtils";
import ImageStore from "game/ImageStore";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";

export default class SlimeRenderer {

	private slime: HTMLImageElement
	private mirroredSlime: OffscreenCanvas;
	readonly fullAnimTime = 1000;
	readonly animFrames = 7;

	constructor(images: ImageStore) {
		let slimeImg = images.get("slime.png").img;
		this.slime = slimeImg;
		let mirroredCanvas = new OffscreenCanvas(slimeImg.width, slimeImg.height);
		let mirroredCtx = mirroredCanvas.getContext("2d")!;
		mirroredCtx.translate(slimeImg.width, 0);
		mirroredCtx.scale(-1, 1);
		for(let i = 0; i < this.animFrames; ++i) {
			mirroredCtx.drawImage(this.slime, i * 32, 64, 32, 32, i*32, 64, 32, 32);
		}
		this.mirroredSlime = mirroredCanvas;
	}

	drawTo(ctx: RenderContext, direction: Direction, [tox, toy]: Position, size: number, activityTime: number) {
		let currentAnimTime = (activityTime % this.fullAnimTime) / this.fullAnimTime;
		let frameIndex = Math.floor(currentAnimTime * this.animFrames);

		let drawSource : HTMLImageElement | OffscreenCanvas = direction === Direction.enum.map.EAST ? this.slime : this.mirroredSlime;

		ctx.drawImage(drawSource, frameIndex * 32, 64, 32, 32, tox - size/2, toy - size/2, size, size);
	}

}
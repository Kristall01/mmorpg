import { RenderContext } from "game/graphics/GraphicsUtils";
import ResourceStore from "game/ResourceStore";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";
import EntityRenderer, { EntityLike } from "./EntityRenderer";

export default class SlimeRenderer extends EntityRenderer{

	private slime: HTMLImageElement
	private mirroredSlime: OffscreenCanvas;
	readonly fullAnimTime = 1000;
	readonly animFrames = 7;

	constructor(images: ResourceStore) {
		super();
		let slimeImg = images.getImage("slime.png").img;
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

	drawTo(ctx: RenderContext, direction: Direction, pos: Position, e: EntityLike, size: number, activityTime: number, translate: [number,number] | "center" = "center") {
		super.renderEntity(ctx, e, pos, size);
		if(translate === "center") {
			translate = [-0.5,-0.5];
		}
		pos[0] += translate[0]*size;
		pos[1] += translate[1]*size;

		let currentAnimTime = (activityTime % this.fullAnimTime) / this.fullAnimTime;
		let frameIndex = Math.floor(currentAnimTime * this.animFrames);

		let drawSource : HTMLImageElement | OffscreenCanvas = direction === Direction.enum.map.EAST ? this.slime : this.mirroredSlime;

		ctx.drawImage(drawSource, frameIndex * 32, 64, 32, 32, pos[0], pos[1], size, size);
	}

}
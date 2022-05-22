import { RenderContext } from "game/graphics/GraphicsUtils";
import ImageStore from "game/ImageStore";
import SkeletonActivity from "visual_model/assetconfig/SkeletonAssetConfig";
import SkeletonEntity from "visual_model/entity/SkeletonEntity";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";
import EntityRenderer, { EntityLike } from "./EntityRenderer";

const spriteDimension = 144;

const rowMap = new Map<number,number>();
const directions = Direction.enum.map;
rowMap.set(directions.EAST.ordinal, 0);
rowMap.set(directions.NORTH.ordinal, 3);
rowMap.set(directions.SOUTH.ordinal, 6);

export default class SkeletonRenderer extends EntityRenderer {

	private skeletonImg: HTMLImageElement
	private mirroredImage: OffscreenCanvas;

	constructor(images: ImageStore) {
		super();
		let mirrorWidth = Math.max(...SkeletonActivity.enum.values.map(val => val.frameCount))*spriteDimension;
		let mirrorHeight = SkeletonActivity.enum.values.length*spriteDimension;

		let skeletonImg = images.get("skeleton.png").img;
		this.skeletonImg = skeletonImg;
		let mirroredCanvas = new OffscreenCanvas(mirrorWidth, mirrorHeight);
		let mirroredCtx = mirroredCanvas.getContext("2d")!;
		mirroredCtx.translate(skeletonImg.width, 0);
		mirroredCtx.scale(-1, 1);

		let maxFrameCount = Math.max(...SkeletonActivity.enum.values.map(v => v.frameCount));

		for(let activity of SkeletonActivity.enum.values) {
			for(let i = 0; i < activity.frameCount; ++i) {
				let vals = [(activity.rowModifier)*spriteDimension, spriteDimension, spriteDimension];
				mirroredCtx.drawImage(this.skeletonImg, i * spriteDimension, vals[0], vals[1], vals[2], (maxFrameCount-i-1) * spriteDimension, vals[0], vals[1], vals[2]);
			}
		}
		this.mirroredImage = mirroredCanvas;
	}

	drawTo(ctx: RenderContext, direction: Direction, pos: Position, skeleton: EntityLike, size: number, activityTime: number, activity: SkeletonActivity) {
		super.renderEntity(ctx, skeleton, pos, size);

		let row: number;
		let drawSource: HTMLImageElement | OffscreenCanvas;

		if(direction === directions.WEST) {
			row = 0;
			drawSource = this.mirroredImage;
		}
		else {
			drawSource = this.skeletonImg;
			row = rowMap.get(direction.ordinal)!;
		}

		let activityAnimTime = activity.animTime;

		let currentAnimTime = (activityTime % activityAnimTime) / activityAnimTime;
		let frameIndex = Math.floor(currentAnimTime * activity.frameCount);
		ctx.drawImage(drawSource, frameIndex * spriteDimension, (row+activity.rowModifier)*spriteDimension, spriteDimension, spriteDimension, pos[0]-(size/2), pos[1] - (size/2), size, size);

		//let drawSource : HTMLImageElement | OffscreenCanvas = direction === Direction.enum.map.EAST ? this.slime : this.mirroredSlime;

		//ctx.drawImage(drawSource, frameIndex * 32, 64, 32, 32, tox - size/2, toy - size/2, size, size);
	}

}
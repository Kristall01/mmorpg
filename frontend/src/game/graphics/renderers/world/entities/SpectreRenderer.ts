import { RenderContext } from "game/graphics/GraphicsUtils";
import ResourceStore from "game/ResourceStore";
import SpectreActivity from "visual_model/assetconfig/SpectreAssetConfig";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";
import EntityRenderer, { EntityLike } from "./EntityRenderer";

const spriteDimension = 102;

const rowMap = new Map<number,number>();
const directions = Direction.enum.map;
rowMap.set(directions.EAST.ordinal, 0);
rowMap.set(directions.NORTH.ordinal, 3);
rowMap.set(directions.SOUTH.ordinal, 6);

export default class SpectreRenderer extends EntityRenderer {

	private opectreImg: HTMLImageElement
	private mirroredImage: OffscreenCanvas;

	constructor(images: ResourceStore) {
		super();
		let mirrorWidth = Math.max(...SpectreActivity.enum.values.map(val => val.frameCount))*spriteDimension;
		let mirrorHeight = SpectreActivity.enum.values.length*spriteDimension;

		let opectreImg = images.getImage("spectre.png").img;
		this.opectreImg = opectreImg;
		let mirroredCanvas = new OffscreenCanvas(mirrorWidth, mirrorHeight);
		let mirroredCtx = mirroredCanvas.getContext("2d")!;
		mirroredCtx.translate(opectreImg.width, 0);
		mirroredCtx.scale(-1, 1);

		let maxFrameCount = Math.max(...SpectreActivity.enum.values.map(v => v.frameCount));

		for(let activity of SpectreActivity.enum.values) {
			for(let i = 0; i < activity.frameCount; ++i) {
				let vals = [(activity.rowModifier)*spriteDimension, spriteDimension, spriteDimension];
				mirroredCtx.drawImage(this.opectreImg, i * spriteDimension, vals[0], vals[1], vals[2], (maxFrameCount-i-1) * spriteDimension, vals[0], vals[1], vals[2]);
			}
		}
		this.mirroredImage = mirroredCanvas;
	}

	drawTo(ctx: RenderContext, direction: Direction, pos: Position, opectre: EntityLike, size: number, activityTime: number, activity: SpectreActivity) {
		super.renderEntity(ctx, opectre, pos, size);

		let row: number;
		let drawSource: HTMLImageElement | OffscreenCanvas;

		if(direction === directions.WEST) {
			row = 0;
			drawSource = this.mirroredImage;
		}
		else {
			drawSource = this.opectreImg;
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
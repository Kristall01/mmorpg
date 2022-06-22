import { RenderContext } from "game/graphics/GraphicsUtils";
import ResourceStore from "game/ResourceStore";
import OgreActivity from "visual_model/assetconfig/OgreAssetConfig";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";
import EntityRenderer, { EntityLike } from "./EntityRenderer";

const spriteDimension = 144;

const rowMap = new Map<number,number>();
const directions = Direction.enum.map;
rowMap.set(directions.EAST.ordinal, 0);
rowMap.set(directions.NORTH.ordinal, 3);
rowMap.set(directions.SOUTH.ordinal, 6);

export default class OgreRenderer extends EntityRenderer {

	private ogreImg: HTMLImageElement
	private mirroredImage: OffscreenCanvas;

	constructor(images: ResourceStore) {
		super();
		let mirrorWidth = Math.max(...OgreActivity.enum.values.map(val => val.frameCount))*spriteDimension;
		let mirrorHeight = OgreActivity.enum.values.length*spriteDimension;

		let ogreImg = images.getImage("ogre.png").img;
		this.ogreImg = ogreImg;
		let mirroredCanvas = new OffscreenCanvas(mirrorWidth, mirrorHeight);
		let mirroredCtx = mirroredCanvas.getContext("2d")!;
		mirroredCtx.translate(ogreImg.width, 0);
		mirroredCtx.scale(-1, 1);

		let maxFrameCount = Math.max(...OgreActivity.enum.values.map(v => v.frameCount));

		for(let activity of OgreActivity.enum.values) {
			for(let i = 0; i < activity.frameCount; ++i) {
				let vals = [(activity.rowModifier)*spriteDimension, spriteDimension, spriteDimension];
				mirroredCtx.drawImage(this.ogreImg, i * spriteDimension, vals[0], vals[1], vals[2], (maxFrameCount-i-1) * spriteDimension, vals[0], vals[1], vals[2]);
			}
		}
		this.mirroredImage = mirroredCanvas;
	}

	drawTo(ctx: RenderContext, direction: Direction, pos: Position, ogre: EntityLike, size: number, activityTime: number, activity: OgreActivity, translate: [number,number] | "center" = "center") {
		super.renderEntity(ctx, ogre, pos, size);
		if(translate === "center") {
			translate = [-0.5,-0.5];
		}
		pos[0] += translate[0]*size;
		pos[1] += translate[1]*size;


		let row: number;
		let drawSource: HTMLImageElement | OffscreenCanvas;

		if(direction === directions.WEST) {
			row = 0;
			drawSource = this.mirroredImage;
		}
		else {
			drawSource = this.ogreImg;
			row = rowMap.get(direction.ordinal)!;
		}

		let activityAnimTime = activity.animTime;

		let currentAnimTime = (activityTime % activityAnimTime) / activityAnimTime;
		let frameIndex = Math.floor(currentAnimTime * activity.frameCount);
		ctx.drawImage(drawSource, frameIndex * spriteDimension, (row+activity.rowModifier)*spriteDimension, spriteDimension, spriteDimension, pos[0], pos[1], size, size);

		//let drawSource : HTMLImageElement | OffscreenCanvas = direction === Direction.enum.map.EAST ? this.slime : this.mirroredSlime;

		//ctx.drawImage(drawSource, frameIndex * 32, 64, 32, 32, tox - size/2, toy - size/2, size, size);
	}

}
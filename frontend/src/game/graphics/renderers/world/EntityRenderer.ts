import CozyPack from "game/graphics/texture/CozyPack";
import { enumValueOf } from "utils";
import Entity from "visual_model/Entity";
import HumanEntity from "visual_model/entity/HumanEntity";
import EntityType from "visual_model/EntityType";
import { ClothColor } from "visual_model/human/HumanAssetConfig";
import { Direction } from "visual_model/Paths";
import WorldRenderer, { RenderConfig } from "./WorldRenderer";

type RendererFunction = (view: WorldRenderer, e: Entity, renderConfig: RenderConfig) => void;

const renderHuman: RendererFunction = (view, e: Entity, renderConfig) => {
	let human = e as HumanEntity;
	let status = human.cachedStatus;
	let [x,y] = status.position;
	let translated = view.translateXY(x, y);
	human.cachedCanvasPosition = translated;

	let activity = view.cozyPack.getCozyActivity(human.activity);
	let sinceTime = status.moving ? renderConfig.rendertime - human.activityStart : 0;
	activity.human(human.skin).drawTo(view.ctx, human.cachedStatus.facing, translated, renderConfig.tileSize*1.5, sinceTime);
	for(let clothes of human.clothes) {
		activity.getCozyCloth(clothes).ofColor(ClothColor.enum.map.BLACK).drawTo(view.ctx, human.cachedStatus.facing, translated, renderConfig.tileSize*1.5, sinceTime);
	}
}

const renderUnknown = (view: WorldRenderer, e: Entity, renderConfig: RenderConfig) => {
	let t = Math.round(renderConfig.rendertime/2) % 1536;
	/* //0, 256, 512, 768, 1024, 1280, 1536, 1792, 2048, 2304
	if(t < 256) {
		ctx.fillStyle = rgb(256, t, 0);
	}
	else if(t < 512) {
		ctx.fillStyle = rgb(512 -t, 256, 0);
	}
	else if(t < 768) {
		ctx.fillStyle = rgb(0, 256, t - 512);
	}
	else if (t < 1024) {
		ctx.fillStyle = rgb(0, 1024-t, 256);
	}
	else if(t < 1280) {
		ctx.fillStyle = rgb(t-1024, 0, 256);
	}
	else {
		ctx.fillStyle = rgb(256, 0, 1536-t);
	} */

	let blockSize = renderConfig.tileSize/2;
	let halfBlockSize = blockSize/2;

	let ctx = view.ctx;

	if(e.cachedStatus.moving) {
		ctx.fillStyle = "lime";
	}
	else {
		ctx.fillStyle = "red";
	}

	let [x,y] = e.cachedStatus.position;
	let translated = view.translateXY(x, y);
	e.cachedCanvasPosition = translated;
	let [cX, cY] = translated;
	view.ctx.fillRect(cX-halfBlockSize, cY-halfBlockSize, blockSize, blockSize);
	ctx.beginPath();
	ctx.moveTo(cX, cY);

	let [xMod, yMod] = e.cachedStatus.facing.modifier;
	ctx.lineTo(cX + xMod*100, cY + yMod*100);
	ctx.stroke();
}

let renderers = new Array<RendererFunction>(EntityType.enum.values.length);

renderers[EntityType.enum.map.HUMAN.ordinal] = renderHuman;
renderers[EntityType.enum.map.UNKNOWN.ordinal] = renderUnknown;

export const renderEntity = (view: WorldRenderer, e: Entity, renderConfig: RenderConfig) => {
	renderers[e.type.ordinal](view, e,renderConfig);
	if(e.name !== null) {
		view.ctx.font = '30px Roboto';

		let metrics = view.ctx.measureText(e.name);
		//let fontHeight = metrics.fontBoundingBoxAscent + metrics.fontBoundingBoxDescent;
		let actualHeight = metrics.actualBoundingBoxAscent + metrics.actualBoundingBoxDescent;

		let textWidth = metrics.width;
		let pos = e.cachedCanvasPosition;
		let xy = [pos[0]-textWidth/2, pos[1]-(e.type.height*1.25*renderConfig.tileSize)];
		view.ctx.textBaseline = "top";

		view.ctx.fillStyle = "rgba(0,0,0,0.3)";
		view.ctx.fillRect(xy[0]-5, xy[1]-5, textWidth+10, actualHeight+10);

		view.ctx.fillStyle = "#fff";
		view.ctx.fillText(e.name, xy[0], xy[1]);
	}
}


/* 	CozyPack.(human.activity)
	cozypack.renderHuman({
		activity: "AXE",
		activityTime: 0,
		facing: this.cachedStatus.facing,
		skin: 1,
		position: translated,
		ctx
	}, translated, ctx); 
	//cozypack.getActivity("WALK").human.ofSkin(this.skin).facing(this.cachedStatus.facing).drawTo(renderconfig.rendertime, ctx, translated, 100);

}
*/
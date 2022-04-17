import { drawBar } from "game/graphics/GraphicsUtils";
import CozyPack from "game/graphics/texture/CozyPack";
import { enumValueOf } from "utils";
import Entity from "visual_model/Entity";
import HumanEntity from "visual_model/entity/HumanEntity";
import EntityType from "visual_model/EntityType";
import { Activity, ClothColor } from "visual_model/human/HumanAssetConfig";
import { Direction } from "visual_model/Paths";
import WorldRenderer, { RenderConfig } from "./WorldRenderer";

type RendererFunction = (view: WorldRenderer, e: Entity, renderConfig: RenderConfig) => void;

export const renderHuman: RendererFunction = (view, e: Entity, renderConfig) => {
	let human = e as HumanEntity;
	let status = human.cachedStatus;
	let [x,y] = status.position;
	let translated = view.translateXY(x, y);
	human.cachedCanvasPosition = translated;

	let alive = e.alive;

	let activity = view.cozyPack.getCozyActivity(alive ? human.activity : Activity.enum.map.DIE);
	let sinceTime: number;
	if(!alive) {
		sinceTime = 0;
	}
	else if(status.moving) {
		sinceTime = renderConfig.rendertime - human.activityStart;
	}
	else {
		sinceTime = 0;
	}
	activity.human(human.skin).drawTo(view.ctx, human.cachedStatus.facing, translated, renderConfig.tileSize*2, sinceTime);
	for(let clothes of human.clothes) {
		activity.getCozyCloth(clothes.cloth).ofColor(clothes.color).drawTo(view.ctx, human.cachedStatus.facing, translated, renderConfig.tileSize*2, sinceTime);
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


	let pos = e.cachedCanvasPosition;
	pos[0] = Math.floor(pos[0]);
	pos[1] = Math.floor(pos[1]);

	let eHeight = e.type.height*1.25 * renderConfig.tileSize;
	let top = pos[1] - eHeight;
	if(e.name !== null) {
		view.ctx.font = '30px Roboto';

		let metrics = view.ctx.measureText(e.name);
		let actualHeight = metrics.actualBoundingBoxAscent + metrics.actualBoundingBoxDescent;

		let boxHeight = actualHeight+10;

		let textWidth = metrics.width;
		let xy = [pos[0]-textWidth/2, top];
		view.ctx.textBaseline = "top";

		view.ctx.fillStyle = "rgba(0,0,0,0.3)";
		view.ctx.fillRect(xy[0]-5, xy[1]-5-boxHeight, textWidth+10, boxHeight);

		view.ctx.fillStyle = "#fff";
		view.ctx.fillText(e.name, xy[0], xy[1] - boxHeight);

	}
	if(e.alive) {
		let hpPercent = e.hp / e.maxHp;
		let rgb = "?";
		if(hpPercent < 0.5) {
			rgb = `rgb(255, ${Math.round(hpPercent*512)},0)`;
		}
		else {
			rgb = `rgb(${(Math.round((1 - hpPercent)*512))},255,0)`;
		}
		drawBar(view.ctx, [pos[0]-50, top], 100, 10, (e.hp / e.maxHp), {fillColor: rgb, borderSize: 2});
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
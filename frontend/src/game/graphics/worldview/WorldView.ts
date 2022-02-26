import VisualModel, { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import { StatelessRenderable } from "../Renderable";
import CozyPack from "../texture/CozyPack";
import TexturePack from "../texture/TexturePack";
import { renderEntity } from "./EntityRenderer";

export interface renderConfig {
	camX: number
	camY: number
	tileSize: number
	width: number
	height: number
	camFocusX: number
	camFocusY: number
	rendertime: number
}

class WorldView extends StatelessRenderable {

	//private camPosition: CameraPositionFn = (rendertime: number) => center;
	private texturePack: TexturePack
	private model: VisualModel
	private renderConfig: renderConfig = null!
	readonly cozyPack: CozyPack;

	constructor(model: VisualModel, cozypack: CozyPack) {
		super();

		this.cozyPack = cozypack;

		this.model = model;
		this.texturePack = TexturePack.getInstance();
	}

	translateXY(x: number, y: number): Position {
		let {camX, camY, tileSize, camFocusX, camFocusY, width, height} = this.renderConfig;

		return [
			(width * camFocusX) + ((-camX + x) * tileSize),
			(height * (camFocusY)) + ((-camY + y) * tileSize)
		];
	}

	translateCanvasXY(x: number, y: number): Position {
		let {tileSize, camFocusX, camFocusY, width, height, camX, camY} = this.renderConfig;

		return [
			(x/tileSize) - ((width * camFocusX)/tileSize) + camX,
			(y/tileSize) - ((height * camFocusY)/tileSize) + camY,
		];
	}

	calculateRenderConfig(rendertime: number, width: number, height: number) {
		let world = this.model.world!;

		let camProps = world.camPosition(rendertime);

		const camFocusX = 0.5;
		const camFocusY = 0.5;

		let zoom = this.model.zoomAt(rendertime);

		let camX = camProps[0];
		let camY = camProps[1];

		if(!this.model.allowCamLeak) {

			let minHorizontalZoom = width / world.width;
			let minVerticalZoom: number = height / world.height;

			if(zoom < minVerticalZoom) {
				zoom = minVerticalZoom;
			}
			if(zoom < minHorizontalZoom) {
				zoom = minHorizontalZoom;
			}
		}

		if(zoom < this.model.maxZoom) {
			zoom = this.model.maxZoom;
		}

		if(height > world.height*zoom) {
			camY = camFocusY * world.height;
		}
		else {
			let minCamY = ((height * camFocusY)/zoom);
			if(camY < minCamY) {
				camY = minCamY;
			}
			else {
				let maxCamY = ((height * camFocusY)/zoom) + this.model.world!.height - (height/zoom);
				if(maxCamY < camY) {
					camY = maxCamY;
				}
			}
		}

		if(width > world.width*zoom) {
			camX = camFocusX * world.width;
		}
		else {
			let minCamX = ((width * camFocusX)/zoom);
			if(camX < minCamX) {
				camX = minCamX;
			}
			else {
				let maxCamX = ((width * camFocusX)/zoom) + this.model.world!.width - (width/zoom);
				if(maxCamX < camX) {
					camX = maxCamX;
				}
			}
		}

		this.renderConfig = {
			camX: camX,
			camY: camY,
			tileSize: zoom,
			width: width,
			height: height,
			camFocusX: camFocusX,
			camFocusY: camFocusY,
			rendertime: rendertime,
		};

	}


	/* calculateRenderConfig(rendertime: number, width: number, height: number) {
		let camProps = this.model.world!.camPosition(rendertime);

		let minHorizontalZoom = width / this.model.world!.width;
		let minVerticalZoom: number = height / this.model.world!.height;

		let zoom = this.model.zoomAt(rendertime);

		if(zoom < minVerticalZoom) {
			zoom = minVerticalZoom;
		}
		if(zoom < minHorizontalZoom) {
			zoom = minHorizontalZoom;
		}
		if(zoom < 40) {
			zoom = 40;
		}

		const camFocusX = 0.5;
		const camFocusY = 0.5;

		let camX = camProps[0];

		let camY = camProps[1];

		let minCamX = ((width * camFocusX)/zoom);

		if(camX < minCamX) {
			camX = minCamX;
		}
		else {
			let maxCamX = ((width * camFocusX)/zoom) + this.model.world!.width - (width/zoom);
			if(maxCamX < camX) {
				camX = maxCamX;
			}
		}

		let minCamY = ((height * camFocusY)/zoom);
		if(camY < minCamY) {
			camY = minCamY;
		}
		else {
			let maxCamY = ((height * camFocusY)/zoom) + this.model.world!.height - (height/zoom);
			if(maxCamY < camY) {
				camY = maxCamY;
			}
		}

		this.renderConfig = {
			camX: camX,
			camY: camY,
			tileSize: zoom,
			width: width,
			height: height,
			camFocusX: camFocusX,
			camFocusY: camFocusY,
		};
	} */

	render(renderTime: number, width: number, height: number): void {
		this.ctx.imageSmoothingEnabled = false;

		if(this.model.world === null || this.model.world.width < 1 || this.model.world.height < 1) {
			this.ctx.fillStyle = "#000";
			this.ctx.fillRect(0, 0, width, height);
			return;
		}

		for(let e of this.model.world.entities) {
			e.calculateStatus(renderTime);
		}

		this.calculateRenderConfig(renderTime, width, height);

		let {camX, camY, tileSize, camFocusX, camFocusY} = this.renderConfig;

		let mostleftX = camX - ((width * camFocusX)/tileSize);
		let mostrightX = camX + (((width * (1-camFocusX)))/tileSize)+1;

		let mostTopY = camY - ((height * camFocusY)/tileSize)
		let mostBotY = camY + (((height * (1-camFocusY)))/tileSize)+1;

		let [mostLeftRender, mostTopRender] = this.translateXY(Math.floor(mostleftX), Math.floor(mostTopY));

		mostLeftRender = Math.floor(mostLeftRender);
		mostTopRender = Math.floor(mostTopRender);

		for(let x = mostleftX, tileRenderX = mostLeftRender; x < mostrightX; ++x, tileRenderX += tileSize) {
			for(let y = mostTopY, tileRenderY = mostTopRender; y < mostBotY; ++y, tileRenderY += tileSize) {
				let floorX = Math.floor(x);
				let floorY = Math.floor(y);
				//let tileXPos = (width * camFocusX) + ((-camX + floorX) * tileSize);
				//let tileYPos = (height * (1-camFocusY)) - ((-camY + floorY+1) * tileSize);

				//let [translatedX, translatedY] = this.translateXY(floorX, floorY);
				this.model.world.getTextureAt(floorX, floorY).drawTo(renderTime, this.ctx, [tileRenderX, tileRenderY], tileSize);
			}
		}
		//this.ctx.fillStyle = "yellow";

		for(let entity of this.model.world.entities) {
			renderEntity(this, entity, this.renderConfig);
		}

		// for(let entity of this.model.world.entities) {
		// 	entity.render(this.renderConfig, this, this.ctx);
		// }

/* 		for(let entity of this.model.world.entities) {
			let [x,y] = entity.getLastPosition();
			let translated = this.translateXY(x, y);
			entity.cachedCanvasPosition = translated;
			let [cX, cY] = translated;
			this.ctx.fillRect(cX-halfBlockSize, cY-halfBlockSize, blockSize, blockSize);
		}
		this.ctx.fillStyle = "#000";
		this.ctx.font = Math.round(tileSize/5)+"px Arial";
		for(let entity of this.model.world.entities) {
			let name = entity.name;
			if(name !== null) {
				let textWidth = this.ctx.measureText(name).width;
				this.ctx.fillText(name, entity.cachedCanvasPosition[0]-textWidth/2, entity.cachedCanvasPosition[1]);
			}
		}
 */	}
	

}

export default WorldView;
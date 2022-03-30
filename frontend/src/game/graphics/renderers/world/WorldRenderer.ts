import { drawText, RenderContext } from "game/graphics/GraphicsUtils";
import Renderable, { StatelessRenderable } from "game/graphics/Renderable";
import CozyPack from "game/graphics/texture/CozyPack";
import EmptyTexture from "game/graphics/texture/EmptyTexture";
import Texture from "game/graphics/texture/Texture";
import TexturePack from "game/graphics/texture/TexturePack";
import VisualResources from "game/VisualResources";
import Matrix from "Matrix";
import SubManager from "SubManager";
import { DEFAULT_MAX_VERSION } from "tls";
import { radiusDistance } from "visual_model/Paths";
import Portal from "visual_model/Portal";
import VisualModel, { Position } from "visual_model/VisualModel";
import World, { WorldEvent } from "visual_model/World";
import { renderEntity } from "./EntityRenderer";
import FloatingItemResource from "./FloatingItemResource";
import { renderLabel, renderLabels } from "./LabelsRenderer";

export interface RenderConfig {
	camX: number
	camY: number
	tileSize: number
	width: number
	height: number
	camFocusX: number
	camFocusY: number
	rendertime: number
}

class WorldRenderer implements Renderable {

	//private camPosition: CameraPositionFn = (rendertime: number) => center;
	private texturePack: TexturePack
	private renderConfig: RenderConfig = null!
	readonly cozyPack: CozyPack;
	private world: World
	private model: VisualModel
	private tileTextureLayers: Array<Matrix<Texture>>;
	private portalIcon: HTMLImageElement
	private floatingItems: Map<number, FloatingItemResource> = new Map();
	private subs: SubManager = new SubManager();
	private visuals: VisualResources
	public ctx: RenderContext = null!;

	constructor(world: World, visuals: VisualResources) {
		this.portalIcon = visuals.images.get("portal.png").img;
		this.visuals = visuals;
		this.model = world.model;
		this.world = world;
		this.texturePack = visuals.textures;
		this.cozyPack = visuals.cozy;
		this.tileTextureLayers = Array.from(world.level.getLayers()).map(a => a[1].map(e => {
			if(e === null) {
				return this.texturePack.getDefaultTexture();
			}
			let t: Texture | undefined = this.texturePack.getTexture(e, "tile");
			if(t === undefined) {
				return this.texturePack.getDefaultTexture();
			}
			return t;
		}));
		//this.tileTextures = world.tileGrid.map(t => visuals.textures.getTexture(t, "tile") || visuals.textures.getDefaultTexture());
		this.updateFloatingItemList();
	}

	unmount(): void {
		this.subs.removeAll();
	}

	mount(ctx: CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D): void {
		this.ctx = ctx;
		this.subs.subscribe(this.world, this.handleEvents.bind(this));
	}

	private updateFloatingItemList() {
		this.floatingItems.clear();
		for(let item of this.world.items) {
			this.floatingItems.set(item.id, {item: item, texture: this.visuals.textures.getTexture(item.item.type, "item") || new EmptyTexture()});
		}
	}

	private handleEvents(e: WorldEvent) {
		if(e === "item") {
			this.updateFloatingItemList();
		}
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
		let world = this.world;

		let camProps = world.camPosition(rendertime);

		const camFocusX = 0.5;
		const camFocusY = 0.5;

		let zoom = this.model.zoomAt(rendertime);

		let camX = camProps[0];
		let camY = camProps[1];

		if(!this.model.allowCamLeak) {
//			if(zoom > 50) {
//				zoom = 50;
//			}
//			else {
				let minHorizontalZoom = width / world.width;
				let minVerticalZoom: number = height / world.height;

				//zoom = Math.max(zoom, Math.min(minVerticalZoom, minHorizontalZoom, 100));


	//			zoom = Math.min(zoom, Math.max(100, minVerticalZoom, minHorizontalZoom))
			if(zoom < Math.min(minVerticalZoom, 100)) {
				zoom = Math.min(minVerticalZoom, 100);
			}
			if(zoom < Math.min(minHorizontalZoom, 100)) {
				zoom = Math.min(minHorizontalZoom, 100);
			}
		}
		zoom = Math.min(zoom, 400);

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
				let maxCamY = ((height * camFocusY)/zoom) + this.world.height - (height/zoom);
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
				let maxCamX = ((width * camFocusX)/zoom) + this.world.width - (width/zoom);
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
		let camProps = this.world.camPosition(rendertime);

		let minHorizontalZoom = width / this.world.width;
		let minVerticalZoom: number = height / this.world.height;

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
			let maxCamX = ((width * camFocusX)/zoom) + this.world.width - (width/zoom);
			if(maxCamX < camX) {
				camX = maxCamX;
			}
		}

		let minCamY = ((height * camFocusY)/zoom);
		if(camY < minCamY) {
			camY = minCamY;
		}
		else {
			let maxCamY = ((height * camFocusY)/zoom) + this.world.height - (height/zoom);
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

		if(this.world.width < 1 || this.world.height < 1) {
			this.ctx.fillStyle = "#000";
			this.ctx.fillRect(0, 0, width, height);
			return;
		}

		for(let e of this.world.entities) {
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

		for(let layer = 0; layer < this.tileTextureLayers.length; ++layer) {
			for(let x = mostleftX, tileRenderX = mostLeftRender; x < mostrightX; ++x, tileRenderX += tileSize) {
				for(let y = mostTopY, tileRenderY = mostTopRender; y < mostBotY; ++y, tileRenderY += tileSize) {
					let floorX = Math.floor(x);
					let floorY = Math.floor(y);
					//let tileXPos = (width * camFocusX) + ((-camX + floorX) * tileSize);
					//let tileYPos = (height * (1-camFocusY)) - ((-camY + floorY+1) * tileSize);
	
					//let [translatedX, translatedY] = this.translateXY(floorX, floorY);
					let t = this.tileTextureLayers[layer].elementAt([floorX, floorY]);
					if(t === null) {
						t = this.texturePack.getDefaultTexture();
					}
					t.drawTo(renderTime, this.ctx, [tileRenderX, tileRenderY], tileSize);
				}
			}
		}
		//this.ctx.fillStyle = "yellow";

		this.ctx.fillStyle = "blue";
		//render portals

		let whRation = this.portalIcon.width / this.portalIcon.height;


		for(let p of this.world.getPortals()) {
			let pWidth = tileSize*p.radius*3;
			let pWidthHalf = pWidth/2;

			let [pX, pY] = this.translateXY(...p.position);
			this.ctx.drawImage(this.portalIcon, pX-pWidthHalf, pY-pWidthHalf, pWidth, pWidth);
		}

/* 		for(let p of this.world.getPortals()) {
			let [pX, pY] = this.translateXY(...p.position);
			this.ctx.beginPath();
			this.ctx.arc(pX, pY, p.radius * tileSize, 0, Math.PI*2);
			this.ctx.fill();
		}
 */
		for(let entity of this.world.entities) {
			renderEntity(this, entity, this.renderConfig);
		}

		renderLabels(this, this.world, this.renderConfig);

		let showPickupMessage = false;

		let tile3 = tileSize / 3;

		for(let item of this.floatingItems.values()) {
			let pos = this.translateXY(...item.item.pos);
			item.texture.drawTo(renderTime, this.ctx, pos, tile3, tile3, -0.5, -1);
			if(!showPickupMessage && (this.world.followedEntity !== null && radiusDistance(item.item.pos, this.world.followedEntity.cachedStatus.position) < 0.5)) {
				showPickupMessage = true;
			}
			if(item.item.item.name !== null) {
				drawText(this.ctx, [pos[0], pos[1] - tile3*1.1], item.item.item.name, "end", "middle");
			}
		}

		if(showPickupMessage) {
//			drawText(this.ctx, [width / 2, height*0.8], "press [A] to pick up items")
			//drawText(this.ctx, [width / 2, height*0.8], "press [A] to pick up items", "middle")
			drawText(this.ctx, [width / 2, height*0.8], "press [A] to pick up items", "middle", "middle",)
		}

		// for(let entity of this.model.world.entities) {
		// for(let entity of this.world.entities) {
		// 	entity.render(this.renderConfig, this, this.ctx);
		// }

/* 		for(let entity of this.world.entities) {
			let [x,y] = entity.getLastPosition();
			let translated = this.translateXY(x, y);
			entity.cachedCanvasPosition = translated;
			let [cX, cY] = translated;
			this.ctx.fillRect(cX-halfBlockSize, cY-halfBlockSize, blockSize, blockSize);
		}
		this.ctx.fillStyle = "#000";
		this.ctx.font = Math.round(tileSize/5)+"px Arial";
		for(let entity of this.world.entities) {
			let name = entity.name;
			if(name !== null) {
				let textWidth = this.ctx.measureText(name).width;
				this.ctx.fillText(name, entity.cachedCanvasPosition[0]-textWidth/2, entity.cachedCanvasPosition[1]);
			}
		}
	 	*/	
	}
	

}

export default WorldRenderer;
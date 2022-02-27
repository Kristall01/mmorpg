import Texture from "game/graphics/texture/Texture";
import TexturePack from "game/graphics/texture/TexturePack";
import Entity from "./Entity";
import VisualModel, { Position } from "./VisualModel";
import { linearMove } from "utils";
import { EntityType } from "./EntityType";
import UnknownEntity from "./entity/UnknownEntity";
import HumanEntity from "./entity/HumanEntity";
import { Direction } from "./Paths";

class World {

	public width: number
	public height: number
	private pack: TexturePack = null!
	private textureMatrix: Array<Texture>
	private _entities: Map<number, Entity> = new Map();
	private humanTextures: null = null;
	camPositionFn: (rendertime: number) => Position;

	constructor(parent: VisualModel, width: number, height: number, tileGrid: string[], camStart: Position) {
		this.width = width;
		this.height = height;
		this.textureMatrix = new Array(width*height);
		this.pack = TexturePack.getInstance();
		this.camPositionFn = () => camStart;

		for(let i = 0; i < tileGrid.length; ++i) {
			this.textureMatrix[i] = this.pack.getTexture(tileGrid[i]);
		}
	}

	get entities(): IterableIterator<Entity> {
		return this._entities.values();
	}

	camPosition(rendertime: number) {
		return this.camPositionFn(rendertime);
	}

	moveCamTo(logicX: number, logicY: number) {
		let now = performance.now();
		let [camX, camY] = this.camPositionFn(now);
		this.camPositionFn = linearMove(camX, camY, now, logicX, logicY, now+1000);
	}

	getEntity(id: number) {
		return this._entities.get(id);
	}

	spawnEntity(id: number, type: EntityType, pos: Position, speed: number, facing: Direction = Direction.enum.map.SOUTH) {
		let e: Entity;
		switch(type) {
 			case EntityType.enum.map.HUMAN: {
				e = new HumanEntity(id, pos, speed, facing);
				break
			}
			default: {
				e = new UnknownEntity(id, pos, speed, facing);
			}
		}
		this._entities.set(id, e);
	}

	despawnEntiy(id: number) {
		this._entities.delete(id);
	}

	followEntity(id: number) {
		let e = this.getEntity(id);
		if(e !== undefined) {
			let a = e;
			this.camPositionFn = () => {
				let pos = a.cachedStatus.position;
				return [pos[0], pos[1]-(a.type.height/2)];
			}
		}
	}

	private posToIndex(pos: Position) {
		return pos[1]*this.width+pos[0];
	}

	private indexToPos(i: number) {
		return [i % this.width, Math.round(i / this.width)];
	}

	getTextureAt(x: number, y: number): Texture {
		if(x < 0 || y < 0 || x >= this.width || y >= this.height) {
			return this.pack.getDefaultTexture();
		}
		let a = this.textureMatrix[this.posToIndex([x,y])];
		if(!a) {
			console.log("err");
		}
		return a;
	}

}

export default World;
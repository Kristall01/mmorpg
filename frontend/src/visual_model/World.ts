import Texture from "game/graphics/texture/Texture";
import TexturePack from "game/graphics/texture/TexturePack";
import Entity from "./Entity";
import VisualModel, { Position } from "./VisualModel";
import { linearMove } from "utils";
import { EntityType } from "./EntityType";
import UnknownEntity from "./entity/UnknownEntity";
import HumanEntity from "./entity/HumanEntity";
import { Direction } from "./Paths";
import { WorldLabel } from "./Label";
import Matrix from "Matrix";
import Portal from "./Portal";
import Item from "./Item";
import FloatingItem from "./FloatingItem";
import UpdateBroadcaster from "./UpdateBroadcaster";

export type WorldEvent = "add-item";

class World extends UpdateBroadcaster<WorldEvent> {

	public width: number
	public height: number
	private _entities: Map<number, Entity> = new Map();
	//private humanTextures: null = null;
	camPositionFn: (rendertime: number) => Position;
	private _labels: WorldLabel[] = [];
	public readonly model: VisualModel
	public readonly tileGrid: Matrix<string>
	private portals: Portal[] = []
	private _items: Map<number, FloatingItem> = new Map();

	constructor(model: VisualModel, width: number, height: number, tileGrid: Matrix<string>, camStart: Position) {
		super();
		this.tileGrid = tileGrid;
		this.model = model;
		this.width = width;
		this.height = height;
		//this.textureMatrix = world
//		this.pack = TexturePack.getInstance();
		this.camPositionFn = () => camStart;

		//this.tex
	}

	addItem(item: FloatingItem) {
		this._items.set(item.id, item);
	}

	get items(): Iterable<FloatingItem> {
		return this._items.values();
	}

	addPortal(p: Portal) {
		this.portals.push(p);
	}

	getPortals(): Iterable<Portal> {
		return this.portals;
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

	addLabel(l: WorldLabel) {
		this._labels.push(l);
	}

	filterLabels(predicate: (value: WorldLabel, index: number) => boolean) {
		if(this._labels.length !== 0) {
			this._labels = this._labels.filter(predicate);
		}
	}

	labels(): Iterable<WorldLabel> {
		return this._labels;
	}

	spawnEntity(id: number, type: EntityType, pos: Position, speed: number, hp: number, maxHp: number, facing: Direction = Direction.enum.map.SOUTH) {
		let e: Entity;
		switch(type) {
 			case EntityType.enum.map.HUMAN: {
				e = new HumanEntity(id, pos, speed, facing, hp, maxHp);
				break
			}
			default: {
				e = new UnknownEntity(id, pos, speed, facing, hp, maxHp);
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

}

export default World;
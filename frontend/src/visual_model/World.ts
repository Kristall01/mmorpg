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
import FloatingItem from "./FloatingItem";
import UpdateBroadcaster from "./UpdateBroadcaster";
import ItemStack from "./ItemStack";
import SlimeEntity from "./entity/SlimeEntity";
import SkeletonEntity from "./entity/SkeletonEntity";
import OgreEntity from "./entity/OgreEntity";
import SpectreEntity from "./entity/SpectreEntity";

export type WorldEvent = "item" | "inventory-update" | "entity-change";

class World extends UpdateBroadcaster<WorldEvent> {

	public width: number
	public height: number
	private _entities: Map<number, Entity<unknown>> = new Map();
	//private humanTextures: null = null;
	camPositionFn: (rendertime: number) => Position;
	private _labels: WorldLabel[] = [];
	public readonly model: VisualModel
	public readonly tileGrid: Array<Matrix<string>>
	private portals: Portal[] = []
	private _items: Map<number, FloatingItem> = new Map();
	public followedEntity: Entity<unknown> | null = null;
	private inventory: Array<ItemStack> = [];


	constructor(model: VisualModel, width: number, height: number, tileGrid: Array<Matrix<string>>, camStart: Position) {
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

	public setInventory(items: Array<ItemStack>) {
		this.inventory = items;
		this.triggerUpdate("inventory-update");
	}

	getItems(): Iterable<ItemStack> {
		return this.inventory;
	}

	spawnItem(item: FloatingItem) {
		this._items.set(item.id, item);
		this.triggerUpdate("item");
	}

	despawnItem(id: number): boolean {
		if(this._items.delete(id)) {
			this.triggerUpdate("item");
			return true;
		}
		return false;
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

	get entities(): IterableIterator<Entity<unknown>> {
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
		let e: Entity<unknown>;
		const entityEnum = EntityType.enum.map;
		switch(type) {
 			case entityEnum.HUMAN: {
				e = new HumanEntity(id, pos, speed, facing, hp, maxHp);
				break
			}
			case entityEnum.SLIME: {
				e = new SlimeEntity(id, pos, speed, hp, maxHp);
				break;
			}
			case entityEnum.SKELETON: {
				e = new SkeletonEntity(id, pos, speed, hp, maxHp);
				break;
			}
			case entityEnum.OGRE: {
				e = new OgreEntity(id, pos, speed, hp, maxHp);
				break
			}
			case entityEnum.SPECTRE: {
				e = new SpectreEntity(id, pos, speed, hp, maxHp);
				break;
			}
			default: {
				e = new UnknownEntity(id, pos, speed, facing, hp, maxHp);
			}
		}
		this._entities.set(id, e);
		this.triggerUpdate("entity-change");
	}

	despawnEntiy(id: number) {
		if(this.followedEntity?.id === id) {
			this.followedEntity = null;
		}
		this._entities.delete(id);
		this.triggerUpdate("entity-change");
	}

	followEntity(id: number) {
		let e = this.getEntity(id);
		if(e !== undefined) {
			this.followedEntity = e;
			let a = e;
			this.camPositionFn = () => {
				let pos = a.cachedStatus.position;
				return [pos[0], pos[1]/* -(a.type.height/2) */];
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
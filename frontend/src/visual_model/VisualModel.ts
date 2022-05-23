import Matrix from "Matrix";
import { SignalIn } from "model/Definitions";
import Entity from "./Entity";
import { LabelType, WorldLabel } from "./Label";
import UpdateBroadcaster from "./UpdateBroadcaster";
import World from "./World";

export type focus = "main" | "chat" | "menu" | "inventory" | "clotheditor";

export type Position = [number,number];


type ZoomFn = (rendertime: number) => number;

export type UpdateTypes = "world"| "chatlog" | "chat-open" | "zoom" | "maxfps" | "dead" | "menu-open" | "focus" | "inventory-open" | "clotheditor-open";

class VisualModel extends UpdateBroadcaster<UpdateTypes> {
	
	private _world: World | null = null;
	public chatlog: Array<string> = []
	chatOpen: boolean = false
	focus: focus = "main"
	menuOpen: boolean = false;
	allowCamLeak: boolean = false;
	private zoomTarget = 200;
	private zoomFn: ZoomFn;
	maxZoom: number = 50;
	private _maxFPS: number | null = null;
	private _dead: boolean = false;
	private listeners = []
	private chatHistory: string[] = [];
	private _inventoryOpen: boolean = false;
	private _clothEditorOpen: boolean = false;

	constructor() {
		super();
		this.handleSignal = this.handleSignal.bind(this);
		this.zoomFn = (rendertime: number) => this.zoomTarget
	}

	public joinWorld(spawnX: number, spawnY: number, width: number, height: number, tileGrid: Array<Matrix<string>>, camStart: Position) {
		this._world = new World(this, width, height, tileGrid, camStart);
		this.triggerUpdate("world");
	}

	public get inventoryOpen() {
		return this._inventoryOpen;
	}

	public get clothEditorOpen() {
		return this._clothEditorOpen;
	}

	setInventoryOpen(value: boolean) {
		if(this.inventoryOpen === value) {
			return;
		}
		this._inventoryOpen = value;
		this.triggerUpdate("inventory-open");
		this.setFocus(value ? "inventory" : "main");
	}

	setClotheditorOpen(value: boolean) {
		if(this.clothEditorOpen === value) {
			return;
		}
		this._clothEditorOpen = value;
		this.triggerUpdate("clotheditor-open");
		this.setFocus(value ? "clotheditor" : "main");
	}

	public pushHistoryEntry(msg: string) {
		if(this.getHistoryEntry(0) !== msg) {
			this.chatHistory.push(msg);
		}
	}

	public getHistoryEntry(index: number): string | undefined {
		let finalIndex = this.chatHistory.length - index-1;
		if(finalIndex === this.chatHistory.length) {
			return "";
		}
		return this.chatHistory[finalIndex];
	}

	public leaveWorld() {
		this._world = null;
		this.triggerUpdate("world");
	}

	get dead() {
		return this._dead;
	}

	set dead(dead: boolean) {
		this._dead = dead;
		this.triggerUpdate("dead");
	}

	get world() {
		return this._world;
	}

	addChatEntry(text: string) {
		this.chatlog = [...this.chatlog, text];
		this.triggerUpdate("chatlog");
	}

	clearChat() {
		this.chatlog = [];
		this.triggerUpdate("chatlog");
	}

	setChatOpen(value: boolean) {
		if(this.chatOpen === value) {
			return;
		}
		this.chatOpen = value;
		this.triggerUpdate("chat-open");
		this.setFocus(value ? "chat" : "main");
	}

	private setFocus(focus: focus) {
		this.focus = focus;
		this.triggerUpdate("focus");
	}

	setMenuOpen(value: boolean) {
		if(this.menuOpen === value) {
			return;
		}
		this.menuOpen = value;
		this.triggerUpdate("menu-open");
		this.setFocus(value ? "menu" : "main");
	}

	handleSignal(signal: SignalIn) {
		signal.execute(this);
	}

	zoomAt(rendertime: number) {
		return this.zoomFn(rendertime);
	}

	setConstantZoom(val: number) {
		this.zoomFn = () => val;
		this.zoomTarget = val;
		this.triggerUpdate("zoom");
	}

	multiplyZoom(val: number) {
		//let from = zoomValue;
		let now = performance.now();
		//let to = zoomValue * val;
		let target = this.zoomTarget*val;
		this.zoomFn = sinSmoothZoom(now, 500, this.zoomFn(now), target);
		this.zoomTarget = target;
		this.triggerUpdate("zoom");
	}

	set maxFPS(fps: number | null) {
		this._maxFPS = fps;
		this.triggerUpdate("maxfps");
	}

	get maxFPS() {
		return this._maxFPS;
	}

	showLabelFor(text: string, labelType: LabelType, entity: Entity<any>) {
		//drawDamageLabel(view.ctx, [pos[0], top+(eHeight*0.75)], (renderConfig.rendertime % 1000)/750, "20");
		this.world?.addLabel(new WorldLabel(text, labelType, entity));
/* 		let eHeight = entity.type.height*1.25 * renderConfig.tileSize;
		this.world?.addLabel(new WorldLabel(text, labelType, t => {
			return [pos[0], top+(eHeight*0.75)];
		}));
 */	}

}

const sinSmoothZoom = (fromTime: number, animationTime: number, fromZoom: number, toZoom: number): ZoomFn => {
	let zoomDiff = fromZoom - toZoom;
	let zoomEnd = fromTime + animationTime;
	let timeWindow = zoomEnd - fromTime;

	return (rendertime: number) =>  {
		if(rendertime > zoomEnd) {
			return toZoom;
		}

		let x = (rendertime - fromTime) / timeWindow;

//		return fromZoom - zoomDiff * Math.sin(x*(Math.PI/2));
		return fromZoom + zoomDiff * (Math.pow(x, 2) - (2*x));
};

}

export default VisualModel;
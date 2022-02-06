import { convertToHtml } from "game/ui/chat/textconverter";
import { SignalIn } from "model/Definitions";
import { linearMove } from "utils";
import Entity from "./Entity";
import World from "./World";

export enum focus {
	main,
	chat
}

export type Position = [number,number];

type ZoomFn = (rendertime: number) => number;

let zoomValue = 100;

class VisualModel {
	
	private _world: World = null!
	public chatlog: Array<string> = []
	private triggerUpdate: () => void
	chatOpen: boolean = false
	focus: focus = focus.main
	entities: Map<number, Entity> = new Map();
	camPositionFn: (rendertime: number) => Position = () => [0,0]
	private zoomFn: ZoomFn = (rendertime: number) => zoomValue;

	constructor() {
		this.triggerUpdate = () => {};
		this._world = new World(this, 30, 30);
		this.handleSignal = this.handleSignal.bind(this);
	}

	camPosition(rendertime: number) {
		return this.camPositionFn(rendertime);
	}

	followEntity(id: number) {
		let e = this.getEntity(id);
		if(e) {
			let a = e;
			this.camPositionFn = (time) => a.getLocation(time);
		}
	}

	moveCamTo(logicX: number, logicY: number) {
		let now = performance.now();
		let [camX, camY] = this.camPositionFn(now);
		this.camPositionFn = linearMove(camX, camY, now, logicX, logicY, now+1000);
	}
	
	setUpdateCallback(changeCallback: () => void) {
		this.triggerUpdate = changeCallback;
	}

	get world() {
		return this._world;
	}

	addChatEntry(text: string) {
		this.chatlog = [...this.chatlog, convertToHtml(text)];
		this.triggerUpdate();
	}

	clearChat() {
		this.chatlog = [];
		this.triggerUpdate();
	}

	setChatOpen(value: boolean) {
		this.chatOpen = value;
		this.focus = value ? focus.chat : focus.main;
		
		this.triggerUpdate();
	}

	getEntity(id: number) {
		return this.entities.get(id);
	}

	handleSignal(signal: SignalIn) {
		signal.execute(this);
	}

	spawnEntity(id: number, type: string, pos: Position, speed: number) {
		this.entities.set(id, new Entity(id, type, pos, speed));
	}

	despawnEntiy(id: number) {
		this.entities.delete(id);
	}

	zoomAt(rendertime: number) {
		return this.zoomFn(rendertime);
	}

	multiplyZoom(val: number) {
		//let from = zoomValue;
		//let to = zoomValue * val;
		zoomValue *= val;
	}
	
}

export default VisualModel;
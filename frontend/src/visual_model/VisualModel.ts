import { convertToHtml } from "game/ui/chat/textconverter";
import { SignalIn } from "model/Definitions";
import World from "./World";

export enum focus {
	main,
	chat
}

export type Position = [number,number];

type ZoomFn = (rendertime: number) => number;

class VisualModel {
	
	private _world: World | null = null;
	public chatlog: Array<string> = []
	private triggerUpdate: () => void
	chatOpen: boolean = false
	focus: focus = focus.main
	allowCamLeak: boolean = false;
	private zoomTarget = 200;
	private zoomFn: ZoomFn;
	maxZoom: number = 40;
	private _maxFPS: number | null = null;

	constructor() {
		this.triggerUpdate = () => {};
		this.handleSignal = this.handleSignal.bind(this);
		this.zoomFn = (rendertime: number) => this.zoomTarget
	}

	setUpdateCallback(changeCallback: () => void) {
		this.triggerUpdate = changeCallback;
	}

	public joinWorld(spawnX: number, spawnY: number, width: number, height: number, tileGrid: string[], camStart: Position) {
		this._world = new World(this, width, height, tileGrid, camStart);
	}

	public leaveWorld() {
		this._world = null;
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

	handleSignal(signal: SignalIn) {
		signal.execute(this);
	}

	zoomAt(rendertime: number) {
		return this.zoomFn(rendertime);
	}

	multiplyZoom(val: number) {
		//let from = zoomValue;
		let now = performance.now();
		//let to = zoomValue * val;
		let target = this.zoomTarget*val;
		this.zoomFn = sinSmoothZoom(now, 500, this.zoomFn(now), target);
		this.zoomTarget = target;
	}

	set maxFPS(fps: number | null) {
		this._maxFPS = fps;
		this.triggerUpdate();
	}

	get maxFPS() {
		return this._maxFPS;
	}

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
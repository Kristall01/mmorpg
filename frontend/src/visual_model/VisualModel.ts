import { convertToHtml } from "game/ui/chat/textconverter";
import { SignalIn } from "model/Definitions";
import UpdateBroadcaster from "./UpdateBroadcaster";
import World from "./World";

export enum focus {
	main,
	chat
}

export type Position = [number,number];

type ZoomFn = (rendertime: number) => number;

class VisualModel extends UpdateBroadcaster {
	
	private _world: World | null = null;
	public chatlog: Array<string> = []
	chatOpen: boolean = false
	focus: focus = focus.main
	allowCamLeak: boolean = false;
	private zoomTarget = 200;
	private zoomFn: ZoomFn;
	maxZoom: number = 40;
	private _maxFPS: number | null = null;
	private listeners = []

	constructor() {
		super();
		this.handleSignal = this.handleSignal.bind(this);
		this.zoomFn = (rendertime: number) => this.zoomTarget
	}

	public joinWorld(spawnX: number, spawnY: number, width: number, height: number, tileGrid: string[], camStart: Position) {
		this._world = new World(this, width, height, tileGrid, camStart);
		this.triggerUpdate("world");
	}

	public leaveWorld() {
		this._world = null;
		this.triggerUpdate("world");
	}

	get world() {
		return this._world;
	}

	addChatEntry(text: string) {
		this.chatlog = [...this.chatlog, convertToHtml(text)];
		this.triggerUpdate("chatlog");
	}

	clearChat() {
		this.chatlog = [];
		this.triggerUpdate("chatlog");
	}

	setChatOpen(value: boolean) {
		this.chatOpen = value;
		this.focus = value ? focus.chat : focus.main;
		
		this.triggerUpdate("chat-open");
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
		this.triggerUpdate("zoom");
	}

	set maxFPS(fps: number | null) {
		this._maxFPS = fps;
		this.triggerUpdate("maxfps");
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
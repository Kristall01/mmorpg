import { convertToHtml } from "game/ui/chat/textconverter";
import { SignalIn } from "model/Definitions";
import World from "./World";

export enum focus {
	main,
	chat
}

export type Position = [number,number];

type ZoomFn = (rendertime: number) => number;

let zoomValue = 100;

class VisualModel {
	
	private _world: World | null = null;
	public chatlog: Array<string> = []
	private triggerUpdate: () => void
	chatOpen: boolean = false
	focus: focus = focus.main
	private zoomFn: ZoomFn = (rendertime: number) => zoomValue;

	constructor() {
		this.triggerUpdate = () => {};
		this.handleSignal = this.handleSignal.bind(this);
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
		//let to = zoomValue * val;
		zoomValue *= val;
	}

}

export default VisualModel;
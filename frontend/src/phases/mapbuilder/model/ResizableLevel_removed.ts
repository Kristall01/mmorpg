import SubManager from "SubManager";
import Level, { LevelEvents } from "visual_model/Level";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";
import TextureGridModel from "./TextureGridModel";

export type ResizableLevelEvents = "resized" | LevelEvents;

export default class ResizableLevel_removed extends UpdateBroadcaster<ResizableLevelEvents>{

	private level: Level
	private subs = new SubManager();

	constructor(level: Level) {
		super();
		this.level = level;
		this.subs.subscribe(level, (e) => this.handlEvent(e))
	}

	handlEvent(e: LevelEvents) {
		this.triggerUpdate(e);
	}

	getLevel(): Level {
		return this.level
	}

	private copyExpand(w: number, h: number, xShift: number, yShift: number) {
		let l = new Level(w, h, false);
		for(let [layerID, layer] of l.getLayers()) {
			let newLayer = l.addLayer();
			newLayer.fill(([x,y]) => {
				return layer.elementAt([x-xShift,y - yShift]);
			})
		}
		this.triggerUpdate("resized");
		return l;
	}

	expandRight(amount: number): void {
		this.copyExpand(this.level.width+amount,this.level.height, 0, 0);
	}

	expandLeft(amount: number) {
		this.copyExpand(this.level.width+amount,this.level.height, amount, 0);
	}

	expandTop(amount: number) {
		this.copyExpand(this.level.width+amount,this.level.height, 0, amount);
	}

	expandBottom(amount: number) {
		this.copyExpand(this.level.width+amount,this.level.height, 0, 0);
	}

}
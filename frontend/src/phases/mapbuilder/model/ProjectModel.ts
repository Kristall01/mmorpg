import TexturePack from "game/graphics/texture/TexturePack";
import ImageStore from "game/ImageStore";
import VisualResources from "game/VisualResources";
import Level from "visual_model/Level";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";
import NamedLevel from "./NamedLevel";

export type ProjectEvents = "world-list" | "world-change";

export default class ProjectModel extends UpdateBroadcaster<ProjectEvents> {

	private levels: Map<string, NamedLevel> = null!;
	private visuals: VisualResources
	//private activeLevel: Level | null = null

	private constructor(visuals: VisualResources, levels: Map<string, NamedLevel>) {
		super();
		this.visuals = visuals;
		this.levels = levels;
	}

	getVisuals(): VisualResources {
		return this.visuals;
	}

	/* getSelectedLevel(): Level | null {
		return this.activeLevel;
	} */

	addLevel(name: string) {
		if(this.levels.has(name)) {
			throw new Error("ez a név már foglalt");
		}
		this.levels.set(name, new NamedLevel(name, new Level(1, 1)));
		this.triggerUpdate("world-list");
	}

	getLevel(name: string): NamedLevel | undefined {
		return this.levels.get(name)
	}

/* 	getActiveLevel(): Level | null {
		return this.activeLevel;
	}

	activateLevel(name: string) {
		console.log("activating "+name);
		let l = this.levels.get(name);
		if(l === undefined) {
			return false;
		}
		this.activeLevel = l;
		this.triggerUpdate("world-select");
	} */

	static load(blob: Blob) {

	}

	getLevels(): Iterable<NamedLevel> {
		return this.levels.values();
	}

	setLevel(name: string, l: Level) {
		this.levels.set(name, new NamedLevel(name, l));
		this.triggerUpdate("world-change");
	}


	static newProjectWithVisuals(visuals: VisualResources) {
		return new ProjectModel(visuals, new Map());
	}

	static newProject(): ProjectModel {
		return new ProjectModel(VisualResources.empty(), new Map());
	}

}
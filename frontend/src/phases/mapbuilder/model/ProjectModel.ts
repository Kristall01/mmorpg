import TexturePack from "game/graphics/texture/TexturePack";
import ImageStore from "game/ImageStore";
import Level from "visual_model/Level";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";

export type ProjectEvents = "world-list";

export default class ProjectModel extends UpdateBroadcaster<ProjectEvents> {

	private tpack: TexturePack = null!;
	private levels: Map<string, Level> = null!;
	//private activeLevel: Level | null = null

	private constructor(tpack: TexturePack, levels: Map<string, Level>) {
		super();
		this.tpack = tpack;
		this.levels = levels;
	}

	getTexturePack() {
		return this.tpack;
	}

	/* getSelectedLevel(): Level | null {
		return this.activeLevel;
	} */

	addLevel(name: string) {
		if(this.levels.has(name)) {
			throw new Error("ez a név már foglalt");
		}
		this.levels.set(name, new Level(1, 1));
		this.triggerUpdate("world-list");
	}

	getLevel(name: string): Level | undefined {
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

	getLevels(): Iterable<[string, Level]> {
		return this.levels.entries();
	}

	static newProject(): ProjectModel {
		return new ProjectModel(new TexturePack(new ImageStore()), new Map());
	}

}
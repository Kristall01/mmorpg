import TexturePack from "game/graphics/texture/TexturePack";
import ImageStore from "game/ImageStore";
import Level from "visual_model/Level";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";

export type ProjectEvents = ""

export default class ProjectModel extends UpdateBroadcaster<ProjectEvents> {

	private tpack: TexturePack = null!;
	private levels: Map<string, Level> = null!;

	private constructor(tpack: TexturePack, levels: Map<string, Level>) {
		super();
		this.tpack = tpack;
		this.levels = levels;
	}

	getTexturePack() {
		return this.tpack;
	}

	static load(blob: Blob) {

	}

	getLevels(): Iterable<[string, Level]> {
		return this.levels.entries();
	}

	static newProject(): ProjectModel {
		return new ProjectModel(new TexturePack(new ImageStore()), new Map());
	}

}
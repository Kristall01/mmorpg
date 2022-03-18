import Matrix from "Matrix";
import { SignalIn } from "model/Definitions";
import Level from "visual_model/Level";
import VisualModel from "visual_model/VisualModel";

export default class SignalJoinworld implements SignalIn {

	spawnX: number;
	spawnY: number;
	level: Level;
	width: number;
	height: number;

	constructor(spawnX: number, spawnY: number, width: number, height: number, level: Level) {
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.level = level;

		this.width = width;
		this.height = height;
	}

	execute(model: VisualModel): void {
		model.joinWorld(this.spawnX, this.spawnY, this.width, this.height, this.level, [this.spawnX, this.spawnY]);
	}

}
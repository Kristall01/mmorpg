import Matrix from "Matrix";
import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalJoinworld implements SignalIn {

	spawnX: number;
	spawnY: number;
	width: number;
	height: number;
	tileGrid: Matrix<string>

	constructor(spawnX: number, spawnY: number, width: number, height: number, tileGrid: string[]) {
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.width = width;
		this.height = height;
		this.tileGrid = Matrix.fromArray(width, height, tileGrid);
	}

	execute(model: VisualModel): void {
		model.joinWorld(this.spawnX, this.spawnY, this.width, this.height, this.tileGrid, [this.spawnX, this.spawnY]);
	}

}
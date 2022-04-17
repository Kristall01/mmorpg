import Matrix from "Matrix";
import { SignalIn } from "model/Definitions";
import Layer from "visual_model/Layer";
import VisualModel from "visual_model/VisualModel";

export default class SignalJoinworld implements SignalIn {

	spawnX: number;
	spawnY: number;
	width: number;
	height: number;
	tileGrid: Array<Matrix<string>>

	constructor(spawnX: number, spawnY: number, width: number, height: number, tileGrid: string[]) {
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.width = width;
		this.height = height;
		let layerCells = width*height;
		if(tileGrid.length % layerCells != 0) {
			throw new Error(`TileGrid length (${tileGrid.length}) not divisible by layer size (${layerCells})`);
		}
		let grids: Array<Matrix<string>> = [];
		for(let i = 0; i < (tileGrid.length/layerCells); ++i) {
			grids.push(Matrix.fromArray(width, height, tileGrid.slice(i*layerCells, (i+1)*layerCells)));
		}
		this.tileGrid = grids;
	}

	execute(model: VisualModel): void {
		model.joinWorld(this.spawnX, this.spawnY, this.width, this.height, this.tileGrid, [this.spawnX, this.spawnY]);
	}

}
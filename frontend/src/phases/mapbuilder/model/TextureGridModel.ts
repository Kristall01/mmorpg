import Texture from "game/graphics/texture/Texture";
import Matrix from "Matrix";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";
import MapbuildModel from "./MapbuildModel";

export type TextureGridModelEvent = "resized"

export default class TextureGridModel extends UpdateBroadcaster<TextureGridModelEvent> {

	public readonly model: MapbuildModel;
	public matrix: Matrix<Texture | null>

	constructor(model: MapbuildModel, width: number, height: number) {
		super();
		this.model = model;
		this.matrix = new Matrix(width, height);
	}

}

/* export function	gridToIndex(x: number, y: number, width: number, height: number): number | null {
	if(x < 0 || y < 0 || x > width || y > height) {
		return null;
	}
	return width*y + x;
}

export function indexToGrid(i: number, width: number, height: number): [number,number] | null {
	if(i < 0 || i > width*height) {
		return null;
	}
	return [i % width, Math.floor(i / width)]
} */
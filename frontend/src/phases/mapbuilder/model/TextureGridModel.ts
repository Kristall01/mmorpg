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

	private copyExpand(t: TextureGridModel, xShift: number, yShift: number) {
		for(let y = 0; y < this.matrix.height; ++y) {
			for(let x = 0; x < this.matrix.width; ++x) {
				t.matrix.setElementAt([x+xShift,y+yShift], this.matrix.elementAt([x,y]));
			}
		}
		this.triggerUpdate("resized");
	}

	expandRight(amount: number): void {
		let t = new TextureGridModel(this.model, this.matrix.width+amount, this.matrix.height);
		this.copyExpand(t, 0, 0);
	}

	expandLeft(amount: number) {
		let t = new TextureGridModel(this.model, this.matrix.width+amount, this.matrix.height);
		this.copyExpand(t, amount, 0);
	}

	expandTop(amount: number) {
		let t = new TextureGridModel(this.model, this.matrix.width, this.matrix.height+amount);
		this.copyExpand(t, 0, amount);
	}

	expandBottom(amount: number) {
		let t = new TextureGridModel(this.model, this.matrix.width, this.matrix.height+amount);
		this.copyExpand(t, 0, 0);
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
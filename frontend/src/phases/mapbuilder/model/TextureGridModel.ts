import Texture from "game/graphics/texture/Texture";
import Matrix from "Matrix";
import { uptime } from "process";
import { Position } from "visual_model/VisualModel";
import MapbuildModel from "./MapbuildModel";

export default class TextureGridModel extends Matrix<Texture | null> {

	public readonly model: MapbuildModel;

	constructor(model: MapbuildModel, width: number, height: number) {
		super(width, height);
		this.model = model;
	}

	private copyExpand(t: TextureGridModel, xShift: number, yShift: number) {
		for(let y = 0; y < this.height; ++y) {
			for(let x = 0; x < this.width; ++x) {
				t.setElementAt([x+xShift,y+yShift], this.elementAt([x,y]));
			}
		}
		this.model.setGrid(t);
	}

	expandRight(amount: number): void {
		let t = new TextureGridModel(this.model, this.width+amount, this.height);
		this.copyExpand(t, 0, 0);
	}

	expandLeft(amount: number) {
		let t = new TextureGridModel(this.model, this.width+amount, this.height);
		this.copyExpand(t, amount, 0);
	}

	expandTop(amount: number) {
		let t = new TextureGridModel(this.model, this.width, this.height+amount);
		this.copyExpand(t, 0, amount);
	}

	expandBottom(amount: number) {
		let t = new TextureGridModel(this.model, this.width, this.height+amount);
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
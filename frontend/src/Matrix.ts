import UpdateBroadcaster from "visual_model/UpdateBroadcaster";
import { Position } from "visual_model/VisualModel";

export type Events = "change"

export default class Matrix<T> extends UpdateBroadcaster<Events> {

	public readonly width: number;
	public readonly height: number;
	public readonly cells: number
	protected elements: Array<Array<T>>;

	constructor(width: number, height: number) {
		super();
		this.width = width;
		this.height = height;
		this.cells = width*height;

		this.elements = new Array(height);
		for(let y = 0; y < height; ++y) {
			this.elements[y] = new Array(width);
		}
	}

	fill(filler: (p: Position) => T) {
		for(let y = 0; y < this.height; ++y) {
			for(let x = 0; x < this.width; ++x) {
				let p: Position = [x,y];
				this.setElementAtUnsafe(p, filler(p));
			}
		}
	}

	elementAt(p: Position): T | null {
		if(p[0] < 0 || p[1] < 0 || p[0] >= this.width || p[1] >= this.height) {
			return null;
		}
		return this.elements[p[1]][p[0]];
	}

	private setElementAtUnsafe(p: Position, t: T) {
		this.elements[p[1]][p[0]] = t;
		this.triggerUpdate("change");
	}

	setElementAt(p: Position, t: T) {
		if(p[0] < 0 || p[1] < 0 || p[0] >= this.width || p[1] >= this.height) {
			throw new Error("IllegalMatrixIndex: "+JSON.stringify(p));
		}
		this.setElementAtUnsafe(p, t);
	}

	map<U>(f: (element: T) => U): Matrix<U> {
		let m = new Matrix<U>(this.width, this.height);
		m.fill(([x,y]) => f(this.elements[y][x]));
		return m;
	}


 	indexToPosition(i: number): Position  {
		if(i < 0 || i > this.cells) {
			throw new Error("IllegalMatrixIndex: "+i);
		}
		return [i % this.width, Math.floor(i / this.width)];
	}
	
 	positionToIndex(p: Position): number {
		if(p[0] < 0 || p[1] < 0 || p[0] > this.width || p[1] > this.height) {
			throw new Error("IllegalMatrixPosition: "+JSON.stringify(p));
		}
		return p[0] + (p[1] * this.width);
	}


	static fromArray<T>(width: number, height: number, a: Array<T>): Matrix<T> {
		if(Math.round((width * height)) != a.length) {
			throw new Error("IllegalMatrixArrayLength: "+JSON.stringify(a));
		}
		let m = new Matrix<T>(width, height);
		m.fill(p => a[m.positionToIndex(p)]);
		return m;
	}

}

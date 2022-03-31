import Matrix from "Matrix";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";

export type LevelEvents = "layer-added";

export type Layer = Matrix<string | null>;

export default class Level extends UpdateBroadcaster<LevelEvents> {

	private layers: Map<number, Layer> = new Map();
	public readonly height: number
	public readonly width: number
	private nextID: number = 0;

	constructor(width: number, height: number, addFirstLayer: boolean = true) {
		super();
		this.width = width;
		this.height = height;

		if(addFirstLayer) {
			this.addLayer();
		}
	}

	getLayer(ID: number): Layer | undefined {
		return this.layers.get(ID);
	}

	addLayer(): Layer {
		let id = this.nextID++;
		let m: Layer = new Matrix(this.width, this.height);
		this.layers.set(id, m);
		this.triggerUpdate("layer-added");
		return m;
	}

	removeLayer(ID: number): boolean {
		return this.layers.delete(ID);
	}

	getLayers(): Iterable<[number,Layer]> {
		return this.layers.entries();
	}

}
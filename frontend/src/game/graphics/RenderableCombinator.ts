import Renderable from "./Renderable";

class LayerMixer implements Renderable {

	private layers: Array<Renderable> = [];
	private ctx: CanvasRenderingContext2D | null = null

	addLayer(r: Renderable) {
		this.layers.push(r);
		if(this.ctx !== null) {
			r.mount(this.ctx);
		}
	}

	removeLayer(r: Renderable) {
		this.layers = this.layers.filter(l => l !== r);
	}

	unmount(): void {
		if(this.ctx !== null)
		for(let i = 0; i < this.layers.length; ++i) {
			this.layers[i].unmount();
		}
		this.ctx = null;
	}

	mount(ctx: CanvasRenderingContext2D): void {
		this.unmount();
		this.ctx = ctx;
		for(let i = 0; i < this.layers.length; ++i) {
			this.layers[i].mount(ctx);
		}
	}

	render(renderTime: number, width: number, height: number): void {
		for(let i = 0; i < this.layers.length; ++i) {
			this.layers[i].render(renderTime, width, height);
		}
	}

}

export default LayerMixer;
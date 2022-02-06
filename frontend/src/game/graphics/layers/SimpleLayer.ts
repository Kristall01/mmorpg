import Renderable from "../Renderable";

abstract class SimpleLayer implements Renderable {

	private ctx: CanvasRenderingContext2D | null = null;

	constructor() {}

	unmount(): void {}

	mount(ctx: CanvasRenderingContext2D): void {
		this.ctx = ctx;
	}

	abstract render: (renderTime: number, width: number, height: number) => void;

}
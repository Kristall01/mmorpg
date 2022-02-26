import { RenderContext } from "./GraphicsUtils";

export default interface Renderable {

	unmount(): void;
	mount(ctx: CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D): void;
	render(renderTime: number, width: number, height: number): void

}

export abstract class StatelessRenderable implements Renderable {

	private _ctx: RenderContext = null!;

	unmount(): void {}

	mount(ctx: RenderContext): void {
		this._ctx = ctx
	}

	get ctx() {
		return this._ctx;
	}

	abstract render(renderTime: number, width: number, height: number): void

}

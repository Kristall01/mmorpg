import { StatelessRenderable } from "game/graphics/Renderable";

export default class EmptyRenderer extends StatelessRenderable {

	static readonly instance: EmptyRenderer = new EmptyRenderer();

	render(renderTime: number, width: number, height: number): void {
		this.ctx.fillStyle = "#000";
		this.ctx.fillRect(0, 0, width, height);
	}

}
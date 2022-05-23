import { StatelessRenderable } from "game/graphics/Renderable";
import Texture from "game/graphics/texture/Texture";

export default class ItemRenderer extends StatelessRenderable {

	private texture: Texture;

	constructor(texture: Texture) {
		super();
		this.texture = texture;
	}

	render(renderTime: number, width: number, height: number): void {
		this.ctx.clearRect(0, 0, width, height);
		this.ctx.imageSmoothingEnabled = false;
		this.texture.drawTo(renderTime, this.ctx, [0, 0], width, height);
	}

}
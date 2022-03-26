import { drawText, RenderContext } from "game/graphics/GraphicsUtils";
import { StatelessRenderable } from "game/graphics/Renderable";
import Texture from "game/graphics/texture/Texture";
import Item from "visual_model/Item";
import { Position } from "visual_model/VisualModel";
import FloatingItemResource from "./FloatingItemResource";

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
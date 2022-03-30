import { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import Texture from "./Texture";

export default class EmptyTexture extends Texture {

	constructor() {
		super("empty");
	}

	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void {
		ctx.fillStyle = "#000";
		ctx.fillRect(position[0], position[1], size, size);
	}

}
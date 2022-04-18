import { Position } from "visual_model/VisualModel";
import { RenderContext } from "../GraphicsUtils";
import Texture from "./Texture";

export default class NullTexture implements Texture {

	private constructor() {}
	
	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void {
	}

	public static instance: NullTexture = new NullTexture();

}
import { RenderContext } from "game/graphics/GraphicsUtils";
import { Position } from "visual_model/VisualModel";
import EntityRenderer, { EntityLike } from "./EntityRenderer";

export default class UnknownEntityRenderer extends EntityRenderer {

	renderEntity(ctx: RenderContext, e: EntityLike, pos: Position, tileSize: number): void {
		super.renderEntity(ctx, e, pos, tileSize);
		ctx.fillStyle = "red";
		let halfTile = tileSize/2;
		ctx.fillRect(pos[0]-halfTile, pos[1]-halfTile, tileSize, tileSize);
	}

}
import { RenderContext } from "game/graphics/GraphicsUtils";
import WorldView, { renderConfig } from "game/graphics/worldview/WorldView";
import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";

const rgb = (r: number,g: number,b: number) => {
	return `rgb(${r}, ${g}, ${b})`;
}

export default class UnknownEntity extends Entity {

	constructor(id: number, loc: Position, speed: number, facing: Direction) {
		super(id, EntityType.enum.map.UNKNOWN, loc, speed, facing);
	}

}
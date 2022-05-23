import { ActivitySnapshot } from "visual_model/ActivityFunction";
import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";

export default class SlimeEntity extends Entity<{}> {
	
	activity(rendertime: number): ActivitySnapshot<{}> {
		return {
			animationTime: rendertime,
			activity: {}
		}
	}
	
	constructor(id: number, loc: Position, speed: number, hp: number, maxHp: number) {
		super(id, EntityType.enum.map.SLIME, loc, speed, Direction.enum.map.EAST, hp, maxHp, "2");
	}

 	attack(pos: Position): void {
		//no animation :/
	}

}
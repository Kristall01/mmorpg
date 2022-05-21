import Entity from "visual_model/Entity";
import { EntityType } from "visual_model/EntityType";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";

export default class SlimeEntity extends Entity {

//	private activityFn: ActivityFunction = idleFunction;
	
	constructor(id: number, loc: Position, speed: number, hp: number, maxHp: number) {
		super(id, EntityType.enum.map.SLIME, loc, speed, Direction.enum.map.EAST, hp, maxHp, "2");
	}

 	attack(pos: Position): void {
/* 		let currentPos = this.cachedStatus.position;
		this.statusFn = facingFunction(calculatedDirection(pos[0]-currentPos[0], pos[1]-currentPos[1]), this.statusFn);
		this.activityFn = createSwordFunction();
 */	}

}
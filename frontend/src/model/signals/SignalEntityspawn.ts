import { SignalIn } from "model/Definitions";
import { enumValueOf } from "utils";
import { EntityType } from "visual_model/EntityType";
import VisualModel, { Position } from "visual_model/VisualModel";

export default class SignalEntityspawn implements SignalIn {

	private id: number
	private type: string
	private pos: Position
	private speed: number

	constructor(id: number, type: string, pos: Position, speed: number) {
		this.id = id;
		this.type = type;
		this.speed = speed;
		this.pos = pos;
	}

	execute(model: VisualModel) {
		let t = enumValueOf(EntityType.enum.map, this.type);
		if(t === null) {
			t = EntityType.enum.map.UNKNOWN;
		}
		model.world?.spawnEntity(this.id, t, this.pos, this.speed);
	}

}
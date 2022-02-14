import { SignalIn } from "model/Definitions";
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
		model.world?.spawnEntity(this.id, this.type, this.pos, this.speed);
	}

}
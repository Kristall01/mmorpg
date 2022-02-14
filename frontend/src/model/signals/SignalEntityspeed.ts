import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalEntityspeed implements SignalIn {

	entityID: number
	speed: number

	constructor(entityID: number, speed: number) {
		this.entityID = entityID;
		this.speed = speed;
	}

	execute(model: VisualModel): void {
		let world = model.world;
		if(world === null) {
			return;
		}
		let e = world.getEntity(this.entityID);
		if(e === undefined) {
			console.warn("network error: entity of ",this.entityID," does not exist clientside.");
			return;
		}
		e.speed = this.speed;
	}

}
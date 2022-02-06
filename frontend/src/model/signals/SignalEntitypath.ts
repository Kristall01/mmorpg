import { SignalIn } from "model/Definitions";
import VisualModel, { Position } from "visual_model/VisualModel";

export default class SignalEntitypath implements SignalIn {

	private id: number
	private points: Position[]
	private startTimeMillis: number

	constructor(id: number, startTimeMillis: number, points: Position[]) {
		this.id = id;
		this.points = points;
		this.startTimeMillis = startTimeMillis
	}

	execute(model: VisualModel) {
		console.log("asd");
		let e = model.getEntity(this.id);
		if(e === undefined) {
			console.warn("INVALID PACKET ERROR: Entity of "+this.id+" does not exist.");
			return;
		}
		if(this.points.length == 0) {
			return;
		}
		e.walkBy(this.startTimeMillis, this.points);
	}

}
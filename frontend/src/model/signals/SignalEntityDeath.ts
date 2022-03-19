import VisualModel from "visual_model/VisualModel";

export default class SignalEntityDeath {

	private entityID: number;

	constructor(entityID: number) {
		this.entityID = entityID;
	}

	execute(model: VisualModel): void {
		let e = model.world?.getEntity(this.entityID);
		if(e === undefined) {
			console.warn("network error: entity of ",this.entityID," does not exist clientside.");
			return;
		}
		e.setDead(true);
	}

}
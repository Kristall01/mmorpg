import { SignalIn } from "model/Definitions";
import Portal from "visual_model/Portal";
import VisualModel from "visual_model/VisualModel";

export default class SignalInPortalspawn implements SignalIn {

	private p: Portal

	constructor(x: number, y: number, radius: number) {
		this.p = new Portal([x,y], radius);
	}

	execute(model: VisualModel): void {
		model.world?.addPortal(this.p);
	}

}
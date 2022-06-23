import { SignalIn } from "model/Definitions";
import Particle from "visual_model/Particle";
import VisualModel from "visual_model/VisualModel";

export default class SignalParticle implements SignalIn {

	private p: Particle;

	constructor(p: Particle) {
		this.p = p;
	}

	execute(model: VisualModel): void {
		model.world?.spawnParticle(this.p);
	}

}
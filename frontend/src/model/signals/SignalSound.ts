import { SignalIn } from "model/Definitions";
import VisualModel from "visual_model/VisualModel";

export default class SignalSound implements SignalIn {

	private soundID: string;

	constructor(soundID: string) {
		this.soundID = soundID;
	}

	execute(model: VisualModel) {
		model.playSound(this.soundID);
	}

}
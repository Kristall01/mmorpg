import Level from "visual_model/Level";

export default class NamedLevel {

	readonly name: string;
	readonly level: Level

	constructor(name: string, level: Level) {
		this.name = name;
		this.level = level;
	}

}
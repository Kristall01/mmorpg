export type ItemEvent = "rename";

export default class Item {

	readonly material: string
	readonly description: Array<string>

	constructor(material: string, description: Array<string>) {
		this.material = material;
		this.description = description;
	}

}
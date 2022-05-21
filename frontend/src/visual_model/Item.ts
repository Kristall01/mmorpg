export type ItemEvent = "rename";

export default class Item {

	readonly type: string
	readonly name: string | null = null;

	constructor(type: string, name?: string) {
		this.type = type;
		this.name = name ?? null;
	}

}
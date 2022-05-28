import parseText, { ParsedText, TextFragment } from "game/ui/chat/textparser";

export type ItemEvent = "rename";

export default class Item {

	readonly material: string
	readonly description: Array<ParsedText>

	constructor(material: string, description: Array<string>) {
		this.material = material;
		this.description = description.map(parseText);
	}

}
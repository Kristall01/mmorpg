import parseText, { ParsedText, TextFragment } from "game/ui/chat/textparser";

export type ItemEvent = "rename";

export type ItemFlags = {
	renderTitle: boolean;
}

export default class Item {

	readonly material: string
	readonly description: Array<ParsedText>
	readonly flags: ItemFlags;

	constructor(material: string, description: Array<string>, flags: ItemFlags) {
		this.material = material;
		this.description = description.map(parseText);
		this.flags = flags;
	}

}
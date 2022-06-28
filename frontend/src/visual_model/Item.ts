import parseText, { ParsedText, TextFragment } from "game/ui/chat/textparser";

export type ItemEvent = "rename";

export type ItemFlags = {
	renderTitle: boolean;
}

export default class Item {

	readonly material: string
	readonly description: Array<ParsedText>
	readonly flags: ItemFlags;
	readonly name: ParsedText;
	readonly type: string

	constructor(type: string, material: string, name: string, description: Array<string>, flags: ItemFlags) {
		this.type = type;
		this.material = material;
		this.name = parseText(name);
		this.description = description.map(parseText);
		this.flags = flags;
	}

}
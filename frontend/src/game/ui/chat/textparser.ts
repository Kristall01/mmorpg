import React from "react";
import { enumValueOf } from "utils";

export class Color {

	readonly code: string;
	readonly hex: string;
	readonly name: string;

	private constructor(code: string, hex: string, name: string) {
		this.code = code;
		this.hex = hex;
		this.name = name;
	}

	static readonly enum = {
		map: {
			BLACK: new Color("0", "#000000", "black"),
			DARK_BLUE: new Color("1", "#0000aa", "dark blue"),
			DARK_GREEN: new Color("2", "#00aa00", "dark green"),
			DARK_AQUA: new Color("3", "#00aaaa", "dark aqua"),
			DARK_RED: new Color("4", "#aa0000", "dark red"),
			DARK_PURPLE: new Color("5", "#aa00aa", "dark purple"),
			GOLD: new Color("6", "#ffaa00", "gold"),
			GRAY: new Color("7", "#aaaaaa", "gray"),
			DARK_GRAY: new Color("8", "#555555", "dark gray"),
			BLUE: new Color("9", "#5555ff", "blue"),
			GREEN: new Color("a", "#55ff55", "green"),
			AQUA: new Color("b", "#55ffff", "aqua"),
			RED: new Color("c", "#ff5555", "red"),
			LIGHT_PURPLE: new Color("d", "#ff55ff", "light purple"),
			YELLOW: new Color("e", "#ffff55", "yellow"),
			WHITE: new Color("f", "#ffffff", "white")
		},
		keymap: new Map<string, Color>(),
		values: new Array<Color>()
	}

	static {
		let values = Object.values(Color.enum.map);
		let map = Color.enum.keymap;
		values.forEach(color => {
			map.set(color.code, color);
		});
	}

}

interface TextFlags {
	bold: boolean
	underline: boolean
	italic: boolean
	crossed: boolean
}

export interface TextFragment {
	color: Color | null,
	text: string,
	flags: TextFlags
}

const baseFlag: TextFlags = Object.freeze({
	bold: false,
	underline: false,
	italic: false,
	crossed: false
});

const styleMap = {
	l: "bold",
	n: "underline",
	o: "italic",
	m: "crossed"
}

const parseText = (text: string): TextFragment[] => {
	let fragments: TextFragment[] = []
	let flagByte: boolean = false
	let bufferedText = "";
	let options: TextFlags = { ...baseFlag };
	let currentColor: Color | null = null;
	for(let c of text) {
		if(c == '§') {
			if(flagByte) {
				bufferedText += '§';
				flagByte = false;
			}
			else {
				flagByte = true;
			}
			continue;
		}
		if(!flagByte) {
			bufferedText += c;
			continue;
		}
		flagByte = false;
		c = c.toLowerCase();
		if(c.match('^[0-9a-f]$')) {
			if(bufferedText.length !== 0) {
				fragments.push({color: currentColor, flags: options, text: bufferedText});
			}
			options = {...baseFlag};
			bufferedText = '';
			currentColor = Color.enum.keymap.get(c) ?? null;
			continue;
		}
		if(c === 'r') {
			if(bufferedText.length !== 0) {
				fragments.push({color: currentColor, flags: options, text: bufferedText});
			}
			options = {...baseFlag};
			bufferedText = '';
			currentColor = null;
			continue;
		}
		let styleKey: string = (styleMap as any)[c];
		if(styleKey !== undefined) {
			if(bufferedText.length !== 0) {
				fragments.push({color: currentColor, flags: options, text: bufferedText});
			}
			options = {...options};
			(options as any)[styleKey] = true;
		}
	}
	if(bufferedText.length !== 0) {
		fragments.push({color: currentColor, flags: options, text: bufferedText});
	}
	return fragments;
}

(window as any).parseText = parseText;

export const parseTextHtml = (text: string): HTMLSpanElement => {
	let fragments = parseText(text);
	let html = document.createElement("span");
	for(let fragment of fragments) {
		let color = fragment.color ?? Color.enum.map.WHITE;
		let flags = fragment.flags;
		let text = fragment.text;
		let style = "";
		if(flags.bold) {
			style += "font-weight: bold;";
		}
		if(flags.underline) {
			style += "text-decoration: underline;";
		}
		if(flags.italic) {
			style += "font-style: italic;";
		}
		if(flags.crossed) {
			style += "text-decoration: line-through;";
		}
		let e = document.createElement("span");
		e.setAttribute("style", `color: ${color.hex}; ${style}"`);
		e.innerText = text;
		html.appendChild(e);
	}
	return html;
}

export const parseTextReactComponent = (text: string) => {
	
}

export default parseText;
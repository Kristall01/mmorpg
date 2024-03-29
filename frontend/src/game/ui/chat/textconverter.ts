interface Dictionary<T> {
	[x: string]: T;
}

interface settings extends Dictionary<boolean> {
	bold: boolean
	underline: boolean
	italic: boolean
	crossed: boolean
}

function generateSpan(settings: settings, color: string | null, text: string): Node | null {
	if(text.length == 0) {
		return null;
	}
	let classes = ['chat'];
	Object.entries(settings).forEach(([key, val]) => {
		if(val) {
			classes.push(key);
		}
	})
	let attributes: Map<string, string> = new Map();
	if(color) {
		if(color.startsWith("#")) {
			attributes.set("color", color);
		}
		else {
			classes.push(color);
		}
	}
	let spanElement = document.createElement("span");
	spanElement.classList.add(...classes);
	spanElement.innerText = text;
	return spanElement;
}

function generateText(settings: settings, color: string | null, text: string) {
	if(text.length == 0) {
		return '';
	}
	let classes = ['chat'];
	Object.entries(settings).forEach(([key, val]) => {
		if(val) {
			classes.push(key);
		}
	})
	let attributes:Dictionary<string> = {};
	if(color) {
		if(color.startsWith("#")) {
			attributes.style = "color:"+color;
		}
		else {
			classes.push(color);
		}
	}
	attributes.class = classes.join(" ");
	return `<span ${Object.entries(attributes).map(([key,val]) => `${key}="${val}"`).join(" ")}>${text}</span>`;
}

const colorMap: Dictionary<string> = {
	'0': 'black',
	'1': 'darkblue',
	'2': 'darkgreen',
	'3': 'darkcyan',
	'4': 'darkred',
	'5': 'purple',
	'6': 'orange',
	'7': 'gray',
	'8': 'darkgray',
	'9': 'blue',
	'a': 'green',
	'b': 'cyan',
	'c': 'red',
	'd': 'magenta',
	'e': 'yellow',
	'f': 'white'
}

const styleMap: Dictionary<string> = {
	'l': 'bold',
	'm': 'crossed',
	'n': 'underline',
	'o': 'italic'
}

const charAsValue = <T>(c: string, obj: T): (keyof T) | undefined => {
	if(!Object.keys(obj).includes(c)) {
		return undefined;
	}
	return (obj as any)[c];
}

export function convertToHtml(input: string): HTMLSpanElement {
	const baseFlag = {bold: false, underline: false, italic: false, crossed: false};

	let output = document.createElement("span");
	let currentBuffer = "";
	let flags: settings = {...baseFlag};
	let color: string | null = null;
	let flagByte = false;
	for(let c of input) {
		if(c == '§') {
			if(flagByte) {
				currentBuffer += '§';
				flagByte = false;
			}
			else {
				flagByte = true;
			}
			continue;
		}
		if(!flagByte) {
/* 			if(c == '<') {
				currentBuffer += "&lt;";
			}
			else if(c == '>') {
				currentBuffer += "&gt;";
			}
			else {
 */				currentBuffer += c;
//			}
			continue;
		}
		flagByte = false;
		if(c.match('^[0-9a-f]$')) {
			let colorCode = (c as keyof typeof colorMap);
			let element = generateSpan(flags, color, currentBuffer);
			if(element !== null) {
				output.appendChild(element);
			}
			currentBuffer = '';
			color = colorMap[colorCode];
			flags = {...baseFlag};
			continue;
		}
		if(c == 'r') {
			let element = generateSpan(flags, color, currentBuffer);
			if(element !== null) {
				output.appendChild(element);
			}
			currentBuffer = '';
			flags = {...baseFlag};
			color = null!;
		}
		let style = styleMap[c];
		if(style) {
			let element = generateSpan(flags, color, currentBuffer);
			if(element !== null) {
				output.appendChild(element);
			}
			currentBuffer = '';
			flags[style] = true;
			continue;
		}
	}
	let element = generateSpan(flags, color, currentBuffer);
	if(element !== null) {
		output.appendChild(element);
	}
	return output;
}


export function convertToHtmlText(input: string) {
	const baseFlag = {bold: false, underline: false, italic: false, crossed: false};

	let output = "";
	let currentBuffer = "";
	let flags: settings = {...baseFlag};
	let color: string | null = null;
	let flagByte = false;
	for(let c of input) {
		if(c == '§') {
			if(flagByte) {
				currentBuffer += '§';
				flagByte = false;
			}
			else {
				flagByte = true;
			}
			continue;
		}
		if(!flagByte) {
			if(c == '<') {
				currentBuffer += "&lt;";
			}
			else if(c == '>') {
				currentBuffer += "&gt;";
			}
			else {
				currentBuffer += c;
			}
			continue;
		}
		flagByte = false;
		if(c.match('^[0-9a-f]$')) {
			output += generateText(flags, color, currentBuffer);
			currentBuffer = '';
			color = colorMap[c];
			flags = {...baseFlag};
			continue;
		}
		if(c == 'r') {
			output += generateText(flags, color, currentBuffer);
			currentBuffer = '';
			flags = {...baseFlag};
			color = null!;
		}
		let style = styleMap[c];
		if(style) {
			output += generateText(flags, color, currentBuffer);
			currentBuffer = '';
			flags[style] = true;
			continue;
		}
	}
	output += generateText(flags, color, currentBuffer);
	return output;
}

import { ReactNode } from "react";
import parseText, { Color, TextFragment } from "./textparser";

export type ChatLineProps = {
	line: string
}

const ChatLine = ({line}: ChatLineProps) => {

	let wordElements: ReactNode[] = [];

	let words: TextFragment[] = parseText(line);

	for(let i = 0; i < words.length; i++) {
		let {color, flags, text} = words[i];
		let {bold, crossed, italic, underline} = flags;
		let style:React.CSSProperties = {};
		if(bold) {
			style.fontWeight = "bold";
		}
		if(crossed) {
			style.textDecoration = "line-through";
		}
		if(italic) {
			style.fontStyle = "italic";
		}
		if(underline) {
			style.textDecoration = "underline";
		}
		if(color !== null) {
			let c = color ?? Color.enum.map.WHITE;
			style.color = c.hex;
		}
		wordElements.push(<span key={i} style={style}>{text}</span>);
	}

	return (
		<span>{wordElements}</span>
	)

};

export default ChatLine;
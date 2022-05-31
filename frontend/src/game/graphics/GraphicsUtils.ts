import { ParsedText, TextFragment } from "game/ui/chat/textparser";
import { Position } from "visual_model/VisualModel";

export function resizeImage(img: CanvasImageSource, width: number, height: number): ImageData {
	let canvas = new OffscreenCanvas(width, height);
	let ctx = canvas.getContext("2d");
	ctx!.imageSmoothingEnabled = false;
	ctx!.drawImage(img, 0, 0, width, height);
	return ctx!.getImageData(0, 0, width, height);
}

interface DrawBarOptions {
	fillColor?: string
	borderSize?: number,
	verticalShift?: number,
	horizontalShift?: number
	width?: number,
	height?: number
}

export const drawBar = (ctx: RenderContext, position: Position, fillLevel: number, options?: DrawBarOptions) => {
	const baseOptions = {
		fillColor: "#0f0",
		borderSize: 1,
		verticalShift: -0.5,
		horizontalShift: 0,
		width: 100,
		height: 10
	};

	let newOptions: DrawBarOptions = options === undefined ? baseOptions : Object.assign({}, baseOptions, options);

	let {borderSize, fillColor, height, horizontalShift, verticalShift, width} = newOptions;

	if(borderSize! % 2 == 1) {
		borderSize! += 1;
	}

	let roundedFillLevel = Math.round(fillLevel*width!);

	let fullWidth = width!+borderSize!;
	let fullHeight = height!+borderSize!;

	//border
	let [posX, posY] = [position[0] - fullWidth*(0.5 - horizontalShift!), position[1] - fullHeight * (0.5 - verticalShift!)].map(Math.floor)//[Math.round(position[0]), Math.round(position[1])];
	ctx.strokeStyle = "#000";
	ctx.lineWidth = borderSize!;
	ctx.strokeRect(posX, posY, fullWidth, fullHeight);

	posX += borderSize!/2;
	posY += borderSize!/2;

	//green
	ctx.fillStyle = fillColor!;
	ctx.fillRect(posX, posY, roundedFillLevel, height!);

	posX += roundedFillLevel;

	//black
	ctx.fillStyle = "#000";
	ctx.fillRect(posX, posY, width!-roundedFillLevel, height!);

	return [width!+borderSize!*2, height!+borderSize!*2];

}

export const drawDamageLabel = (ctx: RenderContext, drawPosition: Position, progress: number, text: string) => {
	let fontText: string;
	let bigTime = 0.2;
	const baseFontSize = 25;
	const increasedFontMultiplier = 1.75;
	if(progress < bigTime) {
		fontText = Math.round((baseFontSize+ baseFontSize*increasedFontMultiplier*(1 - (progress / bigTime))))+"px Roboto";
	}
	else {
		fontText = baseFontSize+"px Roboto";
	}
	ctx.textBaseline = "middle";
	ctx.font = fontText;

	if(progress < 0.5) {
		ctx.fillStyle = "#f00";
	}
	else {
		ctx.fillStyle = `rgba(255,0,0,${1-((progress-0.5)*2)})`
	}
	let p2 = progress*2;
	let posI = Math.pow(p2, 2) - 2*p2;
	let width = ctx.measureText(text).width;
	ctx.fillText(text, drawPosition[0] + p2*40 - width/2, drawPosition[1] + posI*40);
}

export const drawHealLabel = (ctx: RenderContext, drawPosition: Position, progress: number, text: string) => {
	let fontText: string;
	let bigTime = 0.2;
	const baseFontSize = 25;
	const increasedFontMultiplier = 1.75;
	if(progress < bigTime) {
		fontText = Math.round((baseFontSize+ baseFontSize*increasedFontMultiplier*(1 - (progress / bigTime))))+"px Roboto";
	}
	else {
		fontText = baseFontSize+"px Roboto";
	}
	ctx.textBaseline = "middle";
	ctx.font = fontText;
	ctx.fillStyle = "#0f0";
	let width = ctx.measureText(text).width;
	ctx.fillText(text, drawPosition[0] - width/2, drawPosition[1] - progress*50);
}

export const Positioning = {
	start: 0,
	middle: 0.5,
	end: 1
}

export type t = keyof typeof Positioning;

export const drawText = (ctx: RenderContext, canvasPosition: Position, parsedText: ParsedText, vertical: t = "middle", horizontal: t = "middle", paddingRaw: [number,number,number,number]|number = [5,5,5,5]): Position => {

	ctx.font = '30px Roboto';

	let padding = typeof paddingRaw === "number" ? [paddingRaw,paddingRaw,paddingRaw,paddingRaw] : paddingRaw;

	let vModifier = Positioning[vertical];
	let hModifier = Positioning[horizontal];

	ctx.textBaseline = "top";

	let textWidth = 0; //padding[1] + padding[3]

	let maxHeight = -1;

	let fragments = parsedText.fragments;

	let textAssets: Array<[TextFragment, string,number]> = new Array(fragments.length);
	for(let i = 0; i < textAssets.length; ++i) {
		let fontText = "";
		if(fragments[i].flags.italic) {
			fontText += "italic ";
		}
		if(fragments[i].flags.bold) {
			fontText += "bold ";
		}
		fontText += "30px Roboto";
/* 		if(fragments[i].flags.crossed) {
			fontText += "crossed ";
		}
		if(fragments[i].flags.underline) {
			fontText += "underline ";
		}
 */		textAssets[i] = ([fragments[i], fontText, null!]);
	}

	for(let ta of textAssets) {
		let [fragment, font] = ta;
		ctx.font = font;
		let metrics = ctx.measureText(fragment.text);
		let height = /* metrics.actualBoundingBoxAscent */ + metrics.actualBoundingBoxDescent;
		if(height > maxHeight) {
			maxHeight = height;
		}
		let width = metrics.width;
		ta[2] = textWidth;
		textWidth += width;
	

		//let xy = [canvasPosition[0]-width*hModifier, canvasPosition[1]-height*vModifier];

	
		/* ctx.fillStyle = fragment.color?.hex ?? "#fff";
		ctx.fillText(fragment.text, xy[0] + padding[3], xy[1] + padding[0]); */
	}

	let fullWidth = textWidth + padding[1] + padding[3];
	let fullHeight = maxHeight  + padding[0] + padding[2];

	let xy = [canvasPosition[0]-fullWidth*hModifier, canvasPosition[1]-fullHeight*vModifier];
	ctx.fillStyle = "rgba(0,0,0,0.3)";
	ctx.fillRect(xy[0], xy[1], fullWidth, fullHeight);


	let startPos = xy[0] + padding[3];

	for(let [fragment, font, tw] of textAssets) {
		ctx.font = font;
		ctx.fillStyle = fragment.color?.hex ?? "#fff";
		ctx.fillText(fragment.text, startPos + tw, xy[1] + padding[0]);
	}

	return [fullWidth, fullHeight];
	//ORIGINAL

	/* let metrics = ctx.measureText(text);
	let height = /* metrics.actualBoundingBoxAscent *//* + metrics.actualBoundingBoxDescent + padding[0] + padding[2];
	let width = metrics.width + padding[1] + padding[3];

	let xy = [canvasPosition[0]-width*hModifier, canvasPosition[1]-height*vModifier];

	ctx.fillStyle = "rgba(0,0,0,0.3)";
	ctx.fillRect(xy[0], xy[1], width, height);

	ctx.fillStyle = "#fff";
	ctx.fillText(text, xy[0] + padding[3], xy[1] + padding[0]); */
}

export type RenderContext = CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D;
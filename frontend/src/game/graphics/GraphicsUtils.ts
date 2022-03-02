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
	borderSize: number
}

export const drawBar = (ctx: RenderContext, position: Position, width: number, height: number, fillLevel: number, options?: DrawBarOptions): Position => {
	let newOptions: DrawBarOptions = options !== undefined ? options : Object.assign({}, {
		fillColor: "#0f0",
		borderSize: 2
	}, options);

	let [posX, posY] = [Math.round(position[0]), Math.round(position[1])];
	let {borderSize, fillColor} = newOptions;
	ctx.strokeStyle = "#000";
	ctx.lineWidth = borderSize;
	let doubleBorder = borderSize*2;
	ctx.strokeRect(posX - 1, posY - 1, width+borderSize, height+borderSize);
	
	ctx.fillStyle = fillColor!;
	let roundedFillLevel = Math.round(fillLevel*100);
	ctx.fillRect(posX, posY, roundedFillLevel, height);
	ctx.fillStyle = "#000";
	ctx.fillRect(posX+roundedFillLevel, posY, width-roundedFillLevel, height);
	return [0,0];
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

export type RenderContext = CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D;
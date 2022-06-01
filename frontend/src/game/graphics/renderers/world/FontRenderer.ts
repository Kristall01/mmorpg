import { RenderContext } from "game/graphics/GraphicsUtils";
import ImageStore from "game/ResourceStore";
import { Position } from "visual_model/VisualModel";
import codes from "unicode_ascii_w.json";

type codeObj = {
	code: number,
	width: number
}

export default class FontRenderer {

	private img: HTMLImageElement = new Image()

	constructor(images: ImageStore) {
		fetch("/ascii.png").then(r => r.blob()).then(b => URL.createObjectURL(b)).then(url => {
			this.img.src = url;
		});
	}

	drawText(ctx: RenderContext, text: string, position: Position, fontSize: number) {
		let x = Math.round(position[0]);
		let y = Math.round(position[1])
		fontSize = Math.round(fontSize);
		for(let i = 0; i < text.length; ++i) {
			let code = text.charCodeAt(i);
			let obj: codeObj | undefined = (codes as any)[code];
			if(obj === undefined) {
				x += fontSize*1.125;
				continue;
			}
			let widthRatio = obj.width / 16;
			let realWidth = Math.round(widthRatio*fontSize);
			ctx.drawImage(this.img,
				(obj.code % 16)*16,
				Math.floor(obj.code / 16)*16,
				widthRatio*16,
				16,

				x,
				y,
				realWidth,
				fontSize);
			x += realWidth + fontSize/8;
		}
	}


}
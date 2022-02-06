export function resizeImage(img: CanvasImageSource, width: number, height: number): ImageData {
	let canvas = new OffscreenCanvas(width, height);
	let ctx = canvas.getContext("2d");
	ctx!.imageSmoothingEnabled = false;
	ctx!.drawImage(img, 0, 0, width, height);
	return ctx!.getImageData(0, 0, width, height);
}

export type RenderContext = CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D;
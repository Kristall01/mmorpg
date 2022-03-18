function clipImage(image, startX, startY, width, height) {
	return new Promise((accept,reject) => {
		let canvas = document.createElement("canvas");
		canvas.width = width;
		canvas.height = height;
		let ctx = canvas.getContext("2d");
		ctx.drawImage(image, startX, startY, width, height, 0, 0, width, height);
		canvas.toBlob(b => {
			let url = URL.createObjectURL(b);
			let img = new Image(width, height);
			img.crossOrigin = "anonymous";
			img.src = url;
			accept(img);
		})
	});
}
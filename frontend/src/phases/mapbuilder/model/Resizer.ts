import Level from "visual_model/Level";

function copyExpand(oldLevel: Level, width: number, height: number, xShift: number, yShift: number) {
	let newLevel = new Level(width, height, false);
	for(let oldLayer of oldLevel.getLayers()) {
		let newLayer = newLevel.addLayer();
		for(let y = 0; y < oldLevel.height; ++y) {
			for(let x = 0; x < oldLevel.width; ++x) {
				newLayer.setElementAt([x+xShift,y+yShift], oldLayer[1].elementAt([x,y]));
			}
		}
	}
	return newLevel;
}

export function expandRight(amount: number, level: Level): Level {
	//let t = new TextureGridModel(this.model, this.matrix.width+amount, this.matrix.height);
	return copyExpand(level, level.width + amount, level.height, 0, 0);
}

export function expandLeft(amount: number, level: Level) {
	//let t = new TextureGridModel(this.model, this.matrix.width+amount, this.matrix.height);
	return copyExpand(level, level.width + amount, level.height, amount, 0);
}

export function expandTop(amount: number, level: Level) {
	//let t = new TextureGridModel(this.model, this.matrix.width, this.matrix.height+amount);
	return copyExpand(level, level.width, level.height+amount, 0, amount);
}

export function expandBottom(amount: number, level: Level) {
	//let t = new TextureGridModel(this.model, this.matrix.width, this.matrix.height+amount);
	return copyExpand(level, level.width, level.height+amount, 0, 0);
}
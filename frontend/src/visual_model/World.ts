import Texture from "game/graphics/texture/Texture";
import TexturePack from "game/graphics/texture/TexturePack";
import VisualModel, { Position } from "./VisualModel";

class World {

	public width: number
	public height: number
	private pack: TexturePack = null!
	private textureMatrix: Array<Texture>

	constructor(parent: VisualModel, width: number, height: number) {
		this.width = width;
		this.height = height;
		this.textureMatrix = new Array(width*height);
		this.pack = TexturePack.getInstance();

		for(let i = 0; i < this.textureMatrix.length; ++i) {
			this.textureMatrix[i] = (this.pack.getTexture("water"));
		}
		for(let x = 5; x < 25; ++x) {
			for(let y = 5; y < 25; ++y) {
				this.textureMatrix[this.posToIndex([x,y])] = this.pack.getTexture("grass");
			}
		}
	}

	private posToIndex(pos: Position) {
		return pos[1]*this.width+pos[0];
	}

	private indexToPos(i: number) {
		return [i % this.width, Math.round(i / this.width)];
	}

	getTextureAt(x: number, y: number): Texture {
		if(x < 0 || y < 0 || x >= this.width || y >= this.height) {
			return this.pack.getDefaultTexture();
		}
		let a = this.textureMatrix[this.posToIndex([x,y])];
		if(!a) {
			console.log("err");
		}
		return a;
	}

}

export default World;
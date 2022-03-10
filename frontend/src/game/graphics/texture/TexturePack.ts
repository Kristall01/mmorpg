import ImageStore, { loadImage } from "game/ImageStore";
import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import EmptyTexture from "./EmptyTexture";
import StaticTexture from "./StaticTexture";
import Texture, { ofType } from "./Texture";

class TexturePack {

	private textureMap: Map<string, Texture> = new Map();
	private defaultTexture: Texture = new EmptyTexture();
	private images: ImageStore

	constructor(images: ImageStore) {
		this.images = images;
	}

	public addTexture(key: string, t: Texture) {
		this.textureMap.set(key, t);
	}

	public async loadPack(url: string) {
		let srcFileResponse = await fetch(url);
		let textureJson = await srcFileResponse.json();

		Object.entries(textureJson).forEach((entryPair: [string, any]) => {
			let val = entryPair[1];
			let img = this.images.get(val.path);
			this.addTexture(entryPair[0], ofType(entryPair[1].type, img.img, entryPair[1]));
		});

/* 		imageTextures
		let uncheckedDefaTexture = this.textureMap.get(defaultTextureName);
		if(uncheckedDefaTexture === undefined) {
			throw new Error("default texture not found");
		}
		this.defaultTexture = uncheckedDefaTexture;
 */	}

	public getDefaultTexture(): Texture {
		return this.defaultTexture;
	}

	public getTexture(key: string): Texture {
		return this.textureMap.get(key) || this.defaultTexture;
	}

}

export default TexturePack;
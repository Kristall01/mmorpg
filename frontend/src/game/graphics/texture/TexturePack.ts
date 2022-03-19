import ImageStore, { loadImage } from "game/ImageStore";
import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import ColorTexture from "./ColorTexture";
import EmptyTexture from "./EmptyTexture";
import StaticTexture from "./StaticTexture";
import Texture from "./Texture";
import createTexture from "./TextureFactory";

class TexturePack {

	private textureMap: Map<string, Texture> = new Map();
	private defaultTexture: Texture = new EmptyTexture("empty");
	private images: ImageStore

	constructor(images: ImageStore) {
		this.images = images;
		this.addTexture("?", new ColorTexture("blac", "#f00"));
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
			this.addTexture(entryPair[0], createTexture(entryPair[0], entryPair[1].type, img.img, entryPair[1]));
		});

/* 		imageTextures
		let uncheckedDefaTexture = this.textureMap.get(defaultTextureName);
		if(uncheckedDefaTexture === undefined) {
			throw new Error("default texture not found");
		}
		this.defaultTexture = uncheckedDefaTexture;
 */	}

 	getTextures(): Iterable<Texture> {
		return this.textureMap.values();
	}

	public getDefaultTexture(): Texture {
		return this.defaultTexture;
	}

	public getTexture(key: string): Texture {
		return this.textureMap.get(key) || this.defaultTexture;
	}

}

export default TexturePack;
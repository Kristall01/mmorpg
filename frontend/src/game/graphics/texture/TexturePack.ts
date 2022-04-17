import ImageStore, { loadImage } from "game/ImageStore";
import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import EmptyTexture from "./EmptyTexture";
import StaticTexture from "./StaticTexture";
import Texture, { ofType } from "./Texture";

class TexturePack {

	private defaultTexture: Texture = new EmptyTexture();
	private images: ImageStore
	private categories: Map<string, Map<string, Texture>> = new Map();

	constructor(images: ImageStore) {
		this.images = images;
		this.addTexture("null", "tile", NullTexture.instance);
		this.addTexture("empty", "tile", EmptyTexture.instance);
	}

	public addTexture(key: string, categoryName: string, t: Texture) {
		let category = this.categories.get(categoryName);
		if(category === undefined) {
			category = new Map();
			this.categories.set(categoryName, category);
		}
		category.set(key, t);
	}

	public async loadPack(url: string) {
		let srcFileResponse = await fetch(url);
		let textureJson = await srcFileResponse.json();
		let type = textureJson.type;
		if(type === "similar_list") {
			for(let [key, entryData] of Object.entries(textureJson.entries)) {
				let data = Object.assign({}, textureJson.similarity, entryData);
				let {texture_type, path, category} = data;
				let texture = ofType(texture_type, this.images.get(path).img, data);
				this.addTexture(key, category, texture);
			}
			return;
		}
		else if(type === "regular_list") {
			for(let [key, entryData] of Object.entries(textureJson.entries)) {
				let data = (entryData as any);
				let {texture_type, path, category} = data;
				this.addTexture(key, category, ofType(texture_type, this.images.get(path).img, data))
			}
		}

	}

	/* public async loadPack(url: string) {
		let srcFileResponse = await fetch(url);
		let textureJson = await srcFileResponse.json();

		Object.entries(textureJson).forEach((entryPair: [string, any]) => {
			let val = entryPair[1];
			let img = this.images.get(val.path);
			this.addTexture(entryPair[0], ofType(entryPair[1].type, img.img, entryPair[1]));
		}); */

/* 		imageTextures
		let uncheckedDefaTexture = this.textureMap.get(defaultTextureName);
		if(uncheckedDefaTexture === undefined) {
			throw new Error("default texture not found");
		}
		this.defaultTexture = uncheckedDefaTexture;
 */	//}

	public getDefaultTexture(): Texture {
		return this.defaultTexture;
	}

	public getTexture(key: string, category: string): Texture | undefined {
		let map = this.categories.get(category);
		if(map === undefined) {
			return undefined;
		}
		else {
			return map.get(key);
		}
	}

}

export default TexturePack;
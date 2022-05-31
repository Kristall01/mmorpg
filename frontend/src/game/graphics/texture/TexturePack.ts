import ResourceStore from "game/ResourceStore";
import EmptyTexture from "./EmptyTexture";
import NullTexture from "./NullTexture";
import Texture, { ofType } from "./Texture";

class TexturePack {

	private defaultTexture: Texture = new EmptyTexture();
	private images: ResourceStore
	private categories: Map<string, Map<string, Texture>> = new Map();

	constructor(images: ResourceStore) {
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

	private addGeneratedTextures(baseName:string, category: string, textures: Texture | Map<string, Texture>) {
		let a = new Map();
		if((textures as any).__proto__.constructor !== Map) {
			this.addTexture(baseName, category, (textures as Texture));
		}
		else {
			for(let entryPair of (textures as Map<string, Texture>).entries()) {
				let [generatedKey, t] = entryPair;
				this.addTexture(baseName+"_"+generatedKey, category, t);
			}
		}
	}

	public async loadPack(url: string) {
		let srcFileResponse = await fetch(url);
		let textureJson = await srcFileResponse.json();
		let type = textureJson.type;
		if(type === "similar_list") {
			for(let [key, entryData] of Object.entries(textureJson.entries)) {
				let data = Object.assign({}, textureJson.similarity, entryData);
				let {texture_type, path, category} = data;
				let generateResult = ofType(texture_type, this.images.getImage(path).img, data);
				this.addGeneratedTextures(key, category, generateResult);
			}
			return;
		}
		else if(type === "regular_list") {
			for(let [key, entryData] of Object.entries(textureJson.entries)) {
				let data = (entryData as any);
				let {texture_type, path, category} = data;
				let generateResult = ofType(texture_type, this.images.getImage(path).img, data);
				this.addGeneratedTextures(key, category, generateResult);
			}
		}
		else if(type === "index_sprite") {
			for(let [key, entryData] of Object.entries(textureJson.entries)) {
				let data = (entryData as any);
				if(textureJson.similarity !== undefined) {
					data = Object.assign({}, textureJson.similarity, entryData);
				}
				let {texture_type, path, category} = data;
				let texture = ofType(texture_type, this.images.getImage(path).img, data);
				this.addGeneratedTextures(key, category, texture);
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
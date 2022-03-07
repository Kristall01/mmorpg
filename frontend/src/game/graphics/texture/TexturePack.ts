import AnimatedGlobalColumnTexture from "./AnimatedGlobalColumnTexture";
import EmptyTexture from "./EmptyTexture";
import StaticTexture from "./StaticTexture";
import Texture from "./Texture";

class TexturePack {

	private static texturePackInstance: TexturePack = new TexturePack()!

	private textureMap: Map<string, Texture> = new Map();
	private defaultTexture: Texture = new EmptyTexture();

	constructor() {}

	public static async loadAllTextures(url: string) {
		this.texturePackInstance.loadTextures(url);
		let t = new TexturePack();
		await t.loadTextures(url);
		TexturePack.texturePackInstance = t;
	}

	private async loadTextures(url: string) {
		let srcFileResponse = await fetch(url);
		let textureJson = await srcFileResponse.json();

		await Promise.all(Object.entries(textureJson).map(async (entryPair: [string, any]) => {
			let val = entryPair[1]; 
			let img = await this.loadImage("/textures/"+val.path);
			textureJson[entryPair[0]].img = img;
		}));

		Object.entries(textureJson).forEach((entryPair: [string,any]) => {
			switch(entryPair[1].type) {
				case "static_global": {
					this.textureMap.set(entryPair[0], new StaticTexture(entryPair[1].img));
					break;
				}
				case "animated_global_column": {
					this.textureMap.set(entryPair[0], new AnimatedGlobalColumnTexture(entryPair[1].img, entryPair[1].sliceTime));
					break;
				}
			}
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

	private loadImage(path: string): Promise<HTMLImageElement> {
		return new Promise((accept,reject) => {
			let a = new Image();
			a.src = path;
			if(a.complete) {
				accept(a);
				return;
			}
			a.addEventListener("load", () => {
				accept(a);
			});
			a.addEventListener("error", err => {
				reject(err);
			});
		});
	}
}

export default TexturePack;
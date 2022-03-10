import CozyPack from "./graphics/texture/CozyPack";
import TexturePack from "./graphics/texture/TexturePack";
import ImageStore from "./ImageStore";

export default class VisualResources {

	public readonly cozy: CozyPack
	public readonly textures: TexturePack

	private constructor(cozy: CozyPack, texture: TexturePack) {
		this.cozy = cozy;
		this.textures = texture;
	}

	public static async load(): Promise<VisualResources> {
		let images = new ImageStore();
		await images.loadZip("imagestore.zip");
		let cozy = new CozyPack(images);
		let textures = new TexturePack(images);
		await textures.loadPack("texturepack.json");
		return new VisualResources(cozy, textures);
	}

}
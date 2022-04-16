import CozyPack from "./graphics/texture/CozyPack";
import TexturePack from "./graphics/texture/TexturePack";
import ImageStore from "./ImageStore";

export default class VisualResources {

	public readonly cozy: CozyPack
	public readonly textures: TexturePack
	public readonly images: ImageStore

	private constructor(images: ImageStore, cozy: CozyPack, texture: TexturePack) {
		this.cozy = cozy;
		this.textures = texture;
		this.images = images;
	}

	public static async load(): Promise<VisualResources> {
		let images = new ImageStore();
		let zipFiles = ["imagestore.zip", "items.zip"];
		await Promise.all(zipFiles.map(async f => images.loadZip(f)));

		let cozy = new CozyPack(images);
		let textures = new TexturePack(images);

		let textureJsons = ["texturepack.json","items.json"]
		await Promise.all(textureJsons.map(async t => textures.loadPack(t)));
		return new VisualResources(images, cozy, textures);
	}

}
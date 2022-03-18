import CozyPack from "./graphics/texture/CozyPack";
import TexturePack from "./graphics/texture/TexturePack";
import ImageStore from "./ImageStore";

export default class VisualResources {

	private static instance: VisualResources | null = null;

	public readonly cozy: CozyPack
	public readonly textures: TexturePack
	public readonly images: ImageStore

	private constructor(cozy: CozyPack, texture: TexturePack, images: ImageStore) {
		this.cozy = cozy;
		this.textures = texture;
		this.images = images;
	}

	public static async load(): Promise<VisualResources> {
		let baseInstance = VisualResources.instance;
		if(baseInstance !== null) {
			return baseInstance;
		}
		let images = new ImageStore();
		await images.loadZip("imagestore.zip");
		let cozy = new CozyPack(images);
		let textures = new TexturePack(images);
		await textures.loadPack("texturepack.json");
		let instance = new VisualResources(cozy, textures, images);
		VisualResources.instance = instance;
		return instance;
	}

}
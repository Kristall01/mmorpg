import SkeletonRenderer from "./graphics/renderers/world/entities/SkeletonRenderer";
import SlimeRenderer from "./graphics/renderers/world/entities/SlimeRenderer";
import CozyPack from "./graphics/texture/CozyPack";
import TexturePack from "./graphics/texture/TexturePack";
import ImageStore from "./ImageStore";

export default class VisualResources {

	public readonly cozy: CozyPack
	public readonly textures: TexturePack
	public readonly images: ImageStore
	public readonly slimeRenderer: SlimeRenderer
	public readonly skeletonRenderer: SkeletonRenderer

	private constructor(images: ImageStore, cozy: CozyPack, texture: TexturePack, slimeRenderer: SlimeRenderer, skeletonRenderer: SkeletonRenderer) {
		this.cozy = cozy;
		this.textures = texture;
		this.images = images;
		this.slimeRenderer = slimeRenderer;
		this.skeletonRenderer = skeletonRenderer;
	}

	public static async load(): Promise<VisualResources> {
		let images = new ImageStore();
		let zipFiles = ["imagestore.zip", "items.zip", "sprout/sprout.zip", "mystic/mystic.zip","bq.zip"];
		await Promise.all(zipFiles.map(async f => images.loadZip(f)));

		let cozy = new CozyPack(images);
		let textures = new TexturePack(images);
		let slimeRenderer = new SlimeRenderer(images);
		let skeletonRenderer = new SkeletonRenderer(images);

		let textureJsons = ["texturepack.json","items.json","sprout/sprout_index.json"];
		await Promise.all(textureJsons.map(async t => textures.loadPack(t)));
		return new VisualResources(images, cozy, textures, slimeRenderer, skeletonRenderer);
	}

}
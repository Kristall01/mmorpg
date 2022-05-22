import SkeletonRenderer from "./graphics/renderers/world/entities/SkeletonRenderer";
import SlimeRenderer from "./graphics/renderers/world/entities/SlimeRenderer";
import UnknownEntityRenderer from "./graphics/renderers/world/entities/UnknownEntityRenderer";
import HumanRenderer from "./graphics/renderers/world/HumanRenderer";
import CozyPack from "./graphics/texture/CozyPack";
import TexturePack from "./graphics/texture/TexturePack";
import ImageStore from "./ImageStore";

export default class VisualResources {

	public readonly cozy: CozyPack
	public readonly textures: TexturePack
	public readonly images: ImageStore
	public readonly slimeRenderer: SlimeRenderer
	public readonly skeletonRenderer: SkeletonRenderer
	public readonly humanRenderer: HumanRenderer
	public readonly unknownEntityRenderer: UnknownEntityRenderer

	private static instance: VisualResources | null = null;

	private constructor(images: ImageStore, cozy: CozyPack, texture: TexturePack, slimeRenderer: SlimeRenderer, skeletonRenderer: SkeletonRenderer, humanRenderer: HumanRenderer) {
		this.cozy = cozy;
		this.textures = texture;
		this.images = images;
		this.slimeRenderer = slimeRenderer;
		this.skeletonRenderer = skeletonRenderer;
		this.humanRenderer = humanRenderer;
		this.unknownEntityRenderer = new UnknownEntityRenderer();
	}

	public static async load(): Promise<VisualResources> {
		if(this.instance !== null) {
			return this.instance;
		}
		let images = new ImageStore();
		let zipFiles = ["imagestore.zip", "items.zip", "sprout/sprout.zip", "mystic/mystic.zip","bq.zip"];
		await Promise.all(zipFiles.map(async f => images.loadZip(f)));

		let cozy = new CozyPack(images);
		let textures = new TexturePack(images);
		let slimeRenderer = new SlimeRenderer(images);
		let skeletonRenderer = new SkeletonRenderer(images);
		let humanRenderer = new HumanRenderer(cozy);

		let textureJsons = ["texturepack.json","items.json","sprout/sprout_index.json"];
		await Promise.all(textureJsons.map(async t => textures.loadPack(t)));
		let resources = new VisualResources(images, cozy, textures, slimeRenderer, skeletonRenderer, humanRenderer);
		this.instance = resources;
		return resources;
	}

}
import OgreRenderer from "./graphics/renderers/world/entities/OgreRenderer";
import SkeletonRenderer from "./graphics/renderers/world/entities/SkeletonRenderer";
import SlimeRenderer from "./graphics/renderers/world/entities/SlimeRenderer";
import SpectreRenderer from "./graphics/renderers/world/entities/SpectreRenderer";
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
	public readonly ogreRenderer: OgreRenderer
	public readonly humanRenderer: HumanRenderer
	public readonly unknownEntityRenderer: UnknownEntityRenderer
	public readonly spectreRenderer: SpectreRenderer

	private static instance: VisualResources | null = null;

	private constructor(images: ImageStore, cozy: CozyPack, texture: TexturePack, slimeRenderer: SlimeRenderer, skeletonRenderer: SkeletonRenderer, humanRenderer: HumanRenderer, ogreRenderer: OgreRenderer, spectreRenderer: SpectreRenderer) {
		this.cozy = cozy;
		this.textures = texture;
		this.images = images;
		this.slimeRenderer = slimeRenderer;
		this.skeletonRenderer = skeletonRenderer;
		this.ogreRenderer = ogreRenderer;
		this.humanRenderer = humanRenderer;
		this.spectreRenderer = spectreRenderer;
		this.unknownEntityRenderer = new UnknownEntityRenderer();
	}

	public static async load(): Promise<VisualResources> {
		if(this.instance !== null) {
			return this.instance;
		}
		let images = new ImageStore();
		let zipFiles = ["imagestore.zip", "items.zip", "sprout/sprout.zip", "mystic/mystic.zip","bq.zip","items2/items2.zip"];
		await Promise.all(zipFiles.map(async f => images.loadZip(f)));

		let cozy = new CozyPack(images);
		let textures = new TexturePack(images);
		let slimeRenderer = new SlimeRenderer(images);
		let skeletonRenderer = new SkeletonRenderer(images);
		let humanRenderer = new HumanRenderer(cozy);
		let ogreRenderer = new OgreRenderer(images);
		let spectreRenderer = new SpectreRenderer(images);

		let textureJsons = ["texturepack.json","items.json","sprout/sprout_index.json","items2/items2.json"];
		await Promise.all(textureJsons.map(async t => textures.loadPack(t)));
		let resources = new VisualResources(images, cozy, textures, slimeRenderer, skeletonRenderer, humanRenderer, ogreRenderer, spectreRenderer);
		this.instance = resources;
		return resources;
	}

}
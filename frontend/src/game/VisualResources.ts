import OgreRenderer from "./graphics/renderers/world/entities/OgreRenderer";
import SkeletonRenderer from "./graphics/renderers/world/entities/SkeletonRenderer";
import SlimeRenderer from "./graphics/renderers/world/entities/SlimeRenderer";
import SpectreRenderer from "./graphics/renderers/world/entities/SpectreRenderer";
import UnknownEntityRenderer from "./graphics/renderers/world/entities/UnknownEntityRenderer";
import HumanRenderer from "./graphics/renderers/world/HumanRenderer";
import CozyPack from "./graphics/texture/CozyPack";
import TexturePack from "./graphics/texture/TexturePack";
import ImageStore from "./ResourceStore";

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

	private constructor(images: ImageStore, texture: TexturePack) {
		this.textures = texture;
		this.images = images;

		this.cozy = new CozyPack(images);
		this.slimeRenderer = new SlimeRenderer(images);
		this.skeletonRenderer = new SkeletonRenderer(images);
		this.humanRenderer = new HumanRenderer(this.cozy);
		this.ogreRenderer = new OgreRenderer(images);
		this.spectreRenderer = new SpectreRenderer(images);

		this.unknownEntityRenderer = new UnknownEntityRenderer();
	}

	public static async load(): Promise<VisualResources> {
		if(this.instance !== null) {
			return this.instance;
		}
		let images = new ImageStore();
		let imageZipFiles = ["imagestore.zip", "items.zip", "sprout/sprout.zip", "mystic/mystic.zip","bq.zip","items2/items2.zip"];
		let soundZipFiles = ["sound.zip"];
		await Promise.all(imageZipFiles.map(async f => images.loadImageZip(f)));
		await Promise.all(soundZipFiles.map(async f => images.loadSoundZip(f)));
		let textureJsons = ["items.json","sprout/sprout_index.json","items2/items2.json"];
		let textures = new TexturePack(images);

		await Promise.all(textureJsons.map(async t => textures.loadPack(t)));


		let resources = new VisualResources(images, textures);
		this.instance = resources;
		return resources;
	}

}
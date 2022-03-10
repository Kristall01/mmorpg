import { RenderContext } from "game/graphics/GraphicsUtils";
import Renderable, { StatelessRenderable } from "game/graphics/Renderable";
import CozyPack from "game/graphics/texture/CozyPack";
import TexturePack from "game/graphics/texture/TexturePack";
import ImageStore from "game/ImageStore";
import VisualModel from "visual_model/VisualModel";
import World from "visual_model/World";
import EmptyRenderer from "../empty/EmptyRenderer";
import WorldRenderer from "../world/WorldRenderer";

class GameRenderer extends StatelessRenderable {

	//world is assigned by a function called in the constructor
	private world: Renderable = undefined!;
	private model: VisualModel
	public readonly cozyPack: CozyPack
	public readonly texturePack: TexturePack
	p

	constructor(model: VisualModel, cozy: CozyPack, textures: TexturePack) {
		super();

		this.cozyPack = cozy;
		this.texturePack = textures;
		this.model = model;
		model.addUpdateListener(type => {
			if(type === "world") {
				this.updateWorld();
			}
		});
		this.updateWorld();
	}

	private updateWorld() {
		if(this.model.world !== null) {
			this.world = new WorldRenderer(this.model.world, this.cozyPack, this.texturePack);
		}
		else {
			this.world = new EmptyRenderer();
		}
	}

	render(renderTime: number, width: number, height: number): void {
		this.world.render(renderTime, width, height);
	}

	public static async createRenderer(model: VisualModel) {
		let images = await ImageStore.getOrCreateStore("main", (i) => i.loadZip("/imagestore.zip"));
		return new GameRenderer(model, new CozyPack(images), new TexturePack());
	}

}
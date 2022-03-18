import GameView from "game/GameView";
import TexturePack from "game/graphics/texture/TexturePack";
import Matrix from "Matrix";
import GameScene from "phases/game/GameScene";
import TextureGridModel from "./TextureGridModel";
import TabManager from "./TabManager";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";
import NavigatorModel from "./NavigatorModel";
import DModel from "model/impl/demo/DModel";
import TabModel from "./TabModel";
import { NavigationOption } from "./navoptions/NavigationOption";
import VisualResources from "game/VisualResources";
import React from "react";
import NetworkModel from "model/impl/ws/NetworkModel";
import ProjectModel from "./ProjectModel";
import Texture from "game/graphics/texture/Texture";

export type EventTypes = "grid" | "game" | "project";

interface GameViewConfig {
	tab: TabModel
	game: GameScene
}

class MapbuildModel extends UpdateBroadcaster<EventTypes> {

	private wheel: number
	private grid: TextureGridModel | null = null
	private tiles: Texture[] = []
	private activeTexture: Texture | null = null
	private tabManager: TabManager
	public readonly navigator: NavigatorModel = new NavigatorModel();
	private game: TabModel | null = null;
	private project: ProjectModel

	visuals: VisualResources = null!;

	constructor(project: ProjectModel) {
		super();
		this.project = project;
		this.wheel = 50;
		this.tabManager = new TabManager(this);

		this.setGrid(new TextureGridModel(this, 3,3));
		//this.addTile(new Tile("grass.png", "GRASS"));
		//this.addTile(new Tile("water0.png", "WATER"));
	}

	setGrid(grid: TextureGridModel) {
		this.grid = grid;
		this.triggerUpdate("grid");
	}

	getProject() {
		return this.project;
	}

	toggleGame() {
		if(!this.game) {
			let tab = this.tabManager.addTab("game test", t => {
				return React.createElement(GameScene, {visuals: this.visuals, disconnectHandler: () => {
					t.close();
					this.stopGame(t);
				}, modelGenerator: d => new NetworkModel(d, "wss://rpg.ddominik.dev/ws", "TESZT")});
			});
			this.game = tab;
			this.triggerUpdate("game");
		}
		else {
			this.stopGame(this.game);
		}
	}

	private stopGame(g: TabModel) {
		this.game = null;
		g.close();
		this.triggerUpdate("game");
	}

	isGameShown() {
		return this.game;
	}

	setActiveTile(t: Texture | null) {
		this.activeTexture = t;
		//this.update();
	}

	getActiveTexture(): Texture | null {
		return this.activeTexture;
	}

	getWheel(): number{
		return this.wheel;
	}

	setWheel(v: number): void {
		this.wheel = v;
		//this.update();
	}

 	getGrid(): TextureGridModel | null {
		return this.grid;
	}

	getTabManager(): TabManager {
		return this.tabManager;
	}
}



export default MapbuildModel;
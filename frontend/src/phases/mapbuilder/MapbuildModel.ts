import GameView from "game/GameView";
import TexturePack from "game/graphics/texture/TexturePack";
import Matrix from "Matrix";
import GameScene from "phases/game/GameScene";
import Tile from "./model/Tile";
import TileGrid from "./model/TileGrid";

class MapbuildModel {

	private wheel: number
	public readonly update: () => void;
	private grid: TileGrid | null = null
	private tiles: Tile[] = []
	private activeTile: Tile | null = null
	game: boolean = false;

	constructor(update: () => void) {
		this.wheel = 50;
		this.update = update;

		this.setGrid(new TileGrid(this, 3,3));
		this.addTile(new Tile("grass.png", "GRASS"));
		this.addTile(new Tile("water0.png", "WATER"));
	}

	setGrid(grid: TileGrid) {
		this.grid = grid;
		this.update();
	}

	toggleGame() {
		this.game = !this.game;
		this.update();

	}

	setActiveTile(t: Tile | null) {
		this.activeTile = t;
		this.update();
	}

	getActiveTile(): Tile | null {
		return this.activeTile;
	}

	addTile(t: Tile) {
		this.tiles.push(t);
	}

	getWheel(): number{
		return this.wheel;
	}

	setWheel(v: number): void {
		this.wheel = v;
		this.update();
	}

	getGrid(): TileGrid | null {
		return this.grid;
	}

	getTiles(): Iterable<Tile> {
		return this.tiles;
	}

}



export default MapbuildModel;
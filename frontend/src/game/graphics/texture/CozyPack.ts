import { RenderContext } from "game/graphics/GraphicsUtils";
import ImageStore from "game/ImageStore";
import { sortAndDeduplicateDiagnostics } from "typescript";
import { enumValueOf } from "utils";
import { Activity, Cloth, ClothColor, PrimitiveFrametime, Skintone } from "visual_model/human/HumanAssetConfig";
import { Direction } from "visual_model/Paths";
import { Position } from "visual_model/VisualModel";

const CozySize = 32;

//type ActivityType = "AXE" | "BLOCK" | "CARRY" | "DIE" | "FISH" | "HOE" | "HURT" | "JUMP" | "PICKAXE" | "SWORD" | "WALK" | "WATER";

/* enum ActivityType {
	AXE,
	BLOCK,
	CARRY,
	DIE,
	FISH,
	HOE,
	HURT,
	JUMP,
	PICKAXE,
	SWORD,
	WALK,
	WATER
} */

/* class Activity {

	//private static nextOrdinal: number = 0
	//static readonly values: Activity[] = [];
	readonly key: string;
	private _ordinal: number = undefined!;

	static createEnum(images: ImageStore) {
		let a = {
			AXE: new Activity("axe", 0, images),
			BLOCK: new Activity("block", 1, images),
			CARRY: new Activity("carry", 2, images),
			DIE: new Activity("die", 3, images),
			FISH: new Activity("fish", 4, images),
			HOE: new Activity("hoe", 5, images),
			HURT: new Activity("hurt", 6, images),
			JUMP: new Activity("jump", 7, images),
			PICKAXE: new Activity("pickaxe", 8, images),
			SWORD: new Activity("sword", 9, images),
			WALK: new Activity("walk", 10, images),
			WATER: new Activity("water", 11, images),
		} as const;
		return a;
	}

	private constructor(key: string, ordinal: number, images: ImageStore) {
		this.key = key;
	}

} */

interface Drawable {
	drawTo(rendertime: number, ctx: RenderContext, position: Position, size: number): void
}

interface HumanSprite {

	facing(direction: Direction): Drawable;

}

interface HumanPack {

	ofSkin(skin: Skintone): HumanSprite

}

class Sprite {

	readonly img: HTMLImageElement;
	readonly directionMultiplyer: number
	readonly frameTime: PrimitiveFrametime
	private readonly startCorner: Position 

	constructor(img: HTMLImageElement, frameTime: PrimitiveFrametime, startCorner: Position = [0,0]) {
		this.img = img;
		this.frameTime = frameTime;
		this.startCorner = startCorner;

		if(img.height <= CozySize) {
			this.directionMultiplyer = 0;
		}
		else {
			this.directionMultiplyer = CozySize;
		}
	}

	//abstract drawTo(ctx: RenderContext, direction: Direction, [tox, toy]: Position, size: number, activityTime: number): void;

 	drawTo(ctx: RenderContext, direction: Direction, [tox, toy]: Position, size: number, activityTime: number) {
		ctx.drawImage(this.img,
			this.startCorner[0]+ this.frameTime.indexAt(direction, activityTime)*CozySize,
			this.startCorner[1] + direction.ordinal*this.directionMultiplyer,
			CozySize, CozySize, tox - size/2, toy-size, size, size);
	}

/* 	drawTo(ctx: RenderContext, [spriteX, spriteY]: Position, [canvasX, canvasY]: Position, size: number) {
		ctx.drawImage(this.img, (this.startCorner[0]+spriteX)* CozySize, (this.startCorner[1]+ spriteY)*CozySize, CozySize, CozySize, canvasX, canvasY, size, size);
	}
 */
	subSprite(position: Position): Sprite {
		return new Sprite(this.img, this.frameTime, [this.startCorner[0]+position[0], this.startCorner[1]+position[1]])
	}

}


/* class TimelessSprite extends Sprite {

	drawTo(ctx: RenderContext, direction: Direction, [tox, toy]: Position, size: number, activityTime: number) {
		ctx.drawImage(this.img, 0, direction*this.directionMultiplyer, CozySize, CozySize, tox - size/2, toy-size, size, size);
	}
	
}

class TimedSprite extends Sprite {

	readonly frametime: PrimitiveFrametime

	constructor(img: HTMLImageElement, frametime: PrimitiveFrametime) {
		super(img);
		this.frametime = frametime;
	}

	drawTo(ctx: RenderContext, direction: Direction, [tox, toy]: Position, size: number, activityTime: number) {
		let timeMod = activityTime % this.frametime.timeSum(direction)!;
		ctx.drawImage(this.img, this.frametime.indexAt(direction, timeMod), direction*this.directionMultiplyer, CozySize, CozySize, tox - size/2, toy-size, size, size);
	}

} */

interface CozyCloth {

	ofColor(color: ClothColor): Sprite

}

class ColoredCozyCloth implements CozyCloth {

	private clothTextures: Sprite[];

	constructor(key: Activity, cloth: Cloth, images: ImageStore) {
		let baseSprite = new Sprite(images.get(`${cloth.id.toLocaleLowerCase()}_${key.id}.png`).img, key.frametimes);
		let colorValues = ClothColor.enum.values;
		this.clothTextures = new Array(colorValues.length);
		for(let i = 0; i < colorValues.length; ++i) {
			this.clothTextures[i] = baseSprite.subSprite([key.frameCount * CozySize * colorValues[i].ordinal, 0]);
//			this.clothes[i] = new Sprite(images.get(`${clothEnum[i].id}_${key.id}.png`), key.frametimes);
		}
	}

	ofColor(color: ClothColor): Sprite {
		return this.clothTextures[color.ordinal];
	}

}

class ColorlessCozyCloth implements CozyCloth {

	private clothTexture: Sprite;

	constructor(key: Activity, cloth: Cloth, images: ImageStore) {
		this.clothTexture = new Sprite(images.get(`${cloth.id.toLocaleLowerCase()}_${key.id}.png`).img, key.frametimes);
	}

	ofColor(color: ClothColor): Sprite {
		return this.clothTexture;
	}


}

export class CozyActivity {

	private skinTones: Sprite[]
	private clothes: CozyCloth[]

	constructor(key: Activity, images: ImageStore) {
		this.skinTones = new Array<Sprite>(8);
		for(let i = 0; i < 8; ++i) {
			this.skinTones[i] = new Sprite(images.get(`char${i}_${key.id}.png`).img, key.frametimes);
		}
		let clothEnum = Cloth.enum.values;
		this.clothes = new Array<CozyCloth>(clothEnum.length);
		for(let i = 0; i < this.clothes.length; ++i) {
			if(clothEnum[i].colored) {
				this.clothes[i] = new ColoredCozyCloth(key, clothEnum[i], images);
			}
			else {
				this.clothes[i] = new ColorlessCozyCloth(key, clothEnum[i], images);
			}
		}
	}

	human(skin: Skintone) {
		return this.skinTones[skin];
	}

	getCozyCloth(cloth: Cloth) {
		return this.clothes[cloth.ordinal];
	}

}

export default class CozyPack {

	private activities: CozyActivity[];
	private images: ImageStore;

	public constructor(images: ImageStore) {
		this.images = images;

		let objectValues = Activity.enum.values;
		this.activities = new Array(objectValues.length);
		for(let i = 0; i < objectValues.length; ++i) {
			this.activities[i] = new CozyActivity(objectValues[i], images);
		}

//		this.attach(Activity.enum.values, (images, id) => new CozyActivity(id, images));
//		this.clothes = this.attach(Cloth.enum.values, (images, id) => new CozyCloth());
	}

	private attach<T extends {id: string}, U>(e: Array<T>, f: (imgages: ImageStore, id: string) => U): Array<U> {
		let a = new Array(e.length);
		for(let i = 0; i < a.length; ++i) {
			a[i] = f(this.images, e[i].id);
		}
		return a;
	}

	getCozyActivity(act: Activity) {
		return this.activities[act.ordinal];
	}


}

/* interface renderConfig {
	skin: Skintone
	activity: keyof typeof Activity,
	facing: Direction,
	activityTime: number,
	position: Position,
	ctx: RenderContext
} */
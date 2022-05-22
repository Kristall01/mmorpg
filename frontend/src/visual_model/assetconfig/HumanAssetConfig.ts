import { Direction } from "visual_model/Paths";

export type Skintone = 0|1|2|3|4|5|6|7;

export type ClothPosition = "ALL"|"TOP"|"BOTTOM"|"SHOES";

export class ClothColor {

	readonly ordinal: number;
	readonly id: string
	readonly label: string;

	private constructor(ordinal: number, id: string, label: string) {
		this.ordinal = ordinal;
		this.id = id;
		this.label = label;
	}

	static readonly enum = {
		map: {
			BLACK: new ClothColor(0, "BLACK", "fekete"),
			BLUE: new ClothColor(1, "BLUE", "kék"),
			LIGHT_BLUE: new ClothColor(2, "LIGHT_BLUE", "világos kék"),
			BROWN: new ClothColor(3, "BROWN", "barna"),
			GREEN: new ClothColor(4, "GREEN", "zöld"),
			LIGHT_GREEN: new ClothColor(5, "LIGHT_GREEN", "világos zöld"),
			PINK: new ClothColor(6, "PINK", "rózsaszín"),
			PURPLE: new ClothColor(7, "PURPLE", "lila"),
			RED: new ClothColor(8, "RED", "piros"),
			WHITE: new ClothColor(9, "WHITE", "fehér")
		},
		values: new Array<ClothColor>()
	}

	static {
		ClothColor.enum.values = Object.values(ClothColor.enum.map);
	}

}

enum HairColor {
	BLACK = 0,
	BLONDE = 1,
	BROWN = 2,
	BROWN_LIGHT = 3,
	COPPER = 4,
	EMERALD = 5,
	GREEN = 6,
	GREY = 7,
	LILAC = 8,
	NAVY = 9,
	PINK = 10,
	PURPLE = 11,
	RED = 12,
	TURQUOISE = 13
}

export class Hair {

	readonly ordinal: number;
	readonly id: string;
	private static nextOrdinal: number = 0;

	private constructor(id: string) {
		this.id = id;
		this.ordinal = Hair.nextOrdinal++;
	}

	static readonly enum = {
		map: {

			BRAIDS: new Hair('BRAIDS'),// = 'braids',
			BUZZCUT: new Hair('BUZZCUT'),// = 'buzzcut',
			CURLY: new Hair('CURLY'),// = 'curly',
			EMO: new Hair('EMO'),// = 'emo',
			EXTRALONG: new Hair('EXTRALONG'),// = 'extralong',
			FRENCHCURL: new Hair('FRENCHCURL'),// = 'frenchcurl',
			GENTLEMAN: new Hair('GENTLEMAN'),// = 'gentleman',
			MIDIWAVE: new Hair('MIDIWAVE'),// = 'midiwave',
			SPACEBUNS: new Hair('SPACEBUNS'),// = 'spacebuns',
			WAVY: new Hair('WAVY'),// = 'wavy'
		},

		values: new Array<Hair>()
	}

	static {
		Hair.enum.values = Object.values(Hair.enum.map);
	}

}

export class Cloth {

	readonly ordinal: number;
	readonly id: string;
	readonly label: string;
	private static nextOrdinal: number = 0;
	readonly colored: boolean
	readonly position: ClothPosition

	private constructor(id: string, label: string, colored: boolean, pos: ClothPosition) {
		this.id = id;
		this.label = label;
		this.ordinal = Cloth.nextOrdinal++;
		this.colored = colored;
		this.position = pos;
	}

	static readonly enum = {
		map: {
			BASIC: new Cloth('BASIC', 'alap', true, "TOP"),
			CLOWN_BLUE: new Cloth('CLOWN_BLUE', 'kék bohóc', false, "ALL"),
			CLOWN: new Cloth('CLOWN', 'bohóc', false, "ALL"),
			DRESS_WITCH: new Cloth('DRESS_WITCH', 'boszorkány', false, "ALL"),
			FLORAL: new Cloth('FLORAL', 'virágos', true, "TOP"),
			OVERALL: new Cloth('OVERALL', 'overál', true, "TOP"),
			PANTS_SUIT: new Cloth('PANTS_SUIT', 'öltönynadrág', true, "BOTTOM"),
			PANTS: new Cloth('PANTS', 'nadrág', false, "BOTTOM"),
			PUMPKIN_PURPLE: new Cloth('PUMPKIN_PURPLE', 'lila tök', false, "ALL"),
			PUMPKIN: new Cloth('PUMPKIN', 'tök', false, "ALL"),
			SAILOR_BOW: new Cloth('SAILOR_BOW', 'tengerész', true, "TOP"),
			SHOES: new Cloth('SHOES', 'sima cipő', true, "SHOES"),
			SKIRT: new Cloth('SKIRT', 'szoknya', true, "BOTTOM"),
			SPOOKY: new Cloth('SPOOKY', 'csontváz', false, "ALL"),
			SPORTY: new Cloth('SPORTY', 'sportos', true, "TOP"),
			SUIT: new Cloth('SUIT', 'öltöny', true, "TOP"),
		},

		values: new Array<Cloth>()
	}

	static {
		Cloth.enum.values = Object.values(Cloth.enum.map);
	}

}

type stepFunction = (rendertime: number, direction: Direction) => number

export abstract class PrimitiveFrametime {
	abstract indexAt(direction: Direction, rendertime: number): number;
	frameCount: number;

	constructor(frametime: number[]) {
		this.frameCount = frametime.length === 0 ? 1 : frametime.length;
	}

	protected calculateTimeFunction(times: number[]): stepFunction {
		if(times.length == 0) {
			return (t: number) => 0;
		}
		let steps = new Array(times.length);
		let sum = 0;
		for(let i = 0; i < steps.length; ++i) {
			sum += times[i];
			steps[i] = sum;
		}
		let index = 0;
		return (t,d) => {
			t = t % sum
			let i = index;
			if(i == 0 || t > steps[i-1]) {
				for(; i < times.length; ++i) {
					if(t < steps[i]) {
						index = i;
						return i;
					}
				}
			}
			i = 0;
			for(; i < index; ++i) {
				if(t < steps[i]) {
					index = i;
					return i;
				}
			}
			index = 0;
			return 0;
		}
	}

}

export class DirectionlessFrametime extends PrimitiveFrametime {

	readonly frametime: readonly number[]
	//readonly sum: number;
	private timeFn: stepFunction;

	constructor(frametime: number[]) {
		super(frametime);
		this.frametime = Object.freeze([...frametime]);
	/* 	if(this.frametime.length === 0) {
			this.sum = 0;
		}
		else {
			this.sum = Object.freeze(frametime.reduce((a, b) => a+b, 0));
		} */
		this.timeFn = this.calculateTimeFunction(frametime);
	}

	get(d: Direction): readonly number[] {
		return this.frametime;
	}

	indexAt(direction: Direction, rendertime: number) {
		return this.timeFn(rendertime, direction);
	}

}

export class DirectedFrametime extends PrimitiveFrametime {

	//private sum: Array<number>;
	private timeFunctions: stepFunction[];

	constructor(frametime: number[][]) {
		super(frametime[0]);

		let directions = Direction.enum.values;

		//this.sum = new Array(directions.length);
		this.timeFunctions = new Array();
		for(let i = 0; i < directions.length; ++i) {
/* 			if(this.frametime[i].length === 0) {
				this.sum[i] = 0;
			}
			else {
				this.sum[i] = frametime[i].reduce((a, b) => a+b, 0);
			}
 */			this.timeFunctions[i] = this.calculateTimeFunction(frametime[i]);
		}
	}

	indexAt(direction: Direction, rendertime: number): number {
		return this.timeFunctions[direction.ordinal](rendertime, direction);
	}

}

export class HumanActivity {

	readonly ordinal: number;
	readonly id: string;
	readonly frametimes: PrimitiveFrametime
	readonly label: string
	private static nextOrdinal: number = 0;
	readonly frameCount: number

	private constructor(id: string, label: string, frametimes: PrimitiveFrametime) {
		this.id = id;
		this.label = label;
		this.ordinal = HumanActivity.nextOrdinal++;
		this.frametimes = frametimes;
		this.frameCount = frametimes.frameCount;
	}

	static readonly enum = {
		map: {
			AXE: new HumanActivity("axe", "favágás", new DirectionlessFrametime([100, 100, 250, 60, 100])),
			BLOCK: new HumanActivity("block", "védekezés", new DirectionlessFrametime([])),
			CARRY: new HumanActivity("carry", "cipekedés", new DirectionlessFrametime(new Array(8).fill(100))),
			DIE: new HumanActivity("die", "halott", new DirectionlessFrametime([500, 500])),
			FISH: new HumanActivity("fish", "horgászás", new DirectedFrametime([
				[100, 250, 60, 100, 100],
				[100, 250, 60, 100, 100],
				[100, 100, 250, 60, 100],
				[100, 100, 250, 60, 100]
			])),
			HOE: new HumanActivity("hoe", "kapálás", new DirectionlessFrametime([200, 150, 200, 200, 200])),
			HURT: new HumanActivity("hurt", "megsérült", new DirectionlessFrametime([])),
			JUMP: new HumanActivity("jump", "ugrálás", new DirectionlessFrametime([100, 200, 100, 120, 100])),
			PICKAXE: new HumanActivity("pickaxe", "bányászás", new DirectionlessFrametime([100, 100, 250, 60, 100])),
			SWORD: new HumanActivity("sword", "kardozás", new DirectionlessFrametime([100, 200, 80, 100])),
			WALK: new HumanActivity("walk", "sétálás", new DirectionlessFrametime(new Array(8).fill(100))),
			WATER: new HumanActivity("water", "öntözés", new DirectionlessFrametime([300, 600]))
		},
		values: new Array<HumanActivity>()
	}

	static {
		HumanActivity.enum.values = Object.values(HumanActivity.enum.map);
	}

}

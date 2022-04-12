import { enumValueOf } from "utils";
import { Direction } from "visual_model/Paths";

export type Skintone = 0|1|2|3|4|5|6|7;

export type ClothPosition = "ALL"|"TOP"|"BOTTOM"|"SHOES";

export class ClothColor {

	readonly ordinal: number;
	readonly id: string

	private constructor(ordinal: number, id: string) {
		this.ordinal = ordinal;
		this.id = id;
	}

	static readonly enum = {
		map: {
			BLACK: new ClothColor(0, "BLACK"),
			BLUE: new ClothColor(1, "BLUE"),
			LIGHT_BLUE: new ClothColor(2, "LIGHT_BLUE"),
			BROWN: new ClothColor(3, "BROWN"),
			GREEN: new ClothColor(4, "GREEN"),
			LIGHT_GREEN: new ClothColor(5, "LIGHT_GREEN"),
			PINK: new ClothColor(6, "PINK"),
			PURPLE: new ClothColor(7, "PURPLE"),
			RED: new ClothColor(8, "RED"),
			WHITE: new ClothColor(9, "WHITE")
		},
		values: new Array<ClothColor>()
	}

	static {
		ClothColor.enum.values = Object.values(ClothColor.enum.map);
	}

}

enum HairStyle {
	BRAIDS = 'braids',
	BUZZCUT = 'buzzcut',
	CURLY = 'curly',
	EMO = 'emo',
	EXTRALONG = 'extralong',
	FRENCHCURL = 'frenchcurl',
	GENTLEMAN = 'gentleman',
	MIDIWAVE = 'midiwave',
	SPACEBUNS = 'spacebuns',
	WAVY = 'wavy'
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

export class Cloth {

	readonly ordinal: number;
	readonly id: string;
	private static nextOrdinal: number = 0;
	readonly colored: boolean
	readonly position: ClothPosition

	private constructor(id: string, colored: boolean, pos: ClothPosition) {
		this.id = id;
		this.ordinal = Cloth.nextOrdinal++;
		this.colored = colored;
		this.position = pos;
	}

	static readonly enum = {
		map: {
			BASIC: new Cloth('BASIC', true, "TOP"),
			CLOWN_BLUE: new Cloth('CLOWN_BLUE', false, "ALL"),
			CLOWN: new Cloth('CLOWN', false, "ALL"),
			DRESS_WITCH: new Cloth('DRESS_WITCH', false, "ALL"),
			FLORAL: new Cloth('FLORAL', true, "TOP"),
			OVERALL: new Cloth('OVERALL', true, "TOP"),
			PANTS_SUIT: new Cloth('PANTS_SUIT', true, "BOTTOM"),
			PANTS: new Cloth('PANTS', false, "BOTTOM"),
			PUMPKIN_PURPLE: new Cloth('PUMPKIN_PURPLE', false, "ALL"),
			PUMPKIN: new Cloth('PUMPKIN', false, "ALL"),
			SAILOR_BOW: new Cloth('SAILOR_BOW', true, "TOP"),
			SHOES: new Cloth('SHOES', true, "SHOES"),
			SKIRT: new Cloth('SKIRT', true, "BOTTOM"),
			SPOOKY: new Cloth('SPOOKY', false, "ALL"),
			SPORTY: new Cloth('SPORTY', true, "TOP"),
			SUIT: new Cloth('SUIT', true, "TOP"),
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

export class Activity {

	readonly ordinal: number;
	readonly id: string;
	readonly frametimes: PrimitiveFrametime
	private static nextOrdinal: number = 0;
	readonly frameCount: number

	private constructor(id: string, frametimes: PrimitiveFrametime) {
		this.id = id;
		this.ordinal = Activity.nextOrdinal++;
		this.frametimes = frametimes;
		this.frameCount = frametimes.frameCount;
	}

	static readonly enum = {
		map: {
			AXE: new Activity("axe", new DirectionlessFrametime([100, 100, 250, 60, 100])),
			BLOCK: new Activity("block", new DirectionlessFrametime([])),
			CARRY: new Activity("carry", new DirectionlessFrametime(new Array(8).fill(100))),
			DIE: new Activity("die", new DirectionlessFrametime([500, 500])),
			FISH: new Activity("fish", new DirectedFrametime([
				[100, 250, 60, 100, 100],
				[100, 250, 60, 100, 100],
				[100, 100, 250, 60, 100],
				[100, 100, 250, 60, 100]
			])),
			HOE: new Activity("hoe", new DirectionlessFrametime([200, 150, 200, 200, 200])),
			HURT: new Activity("hurt", new DirectionlessFrametime([])),
			JUMP: new Activity("jump", new DirectionlessFrametime([100, 200, 100, 120, 100])),
			PICKAXE: new Activity("pickaxe", new DirectionlessFrametime([100, 100, 250, 60, 100])),
			SWORD: new Activity("sword", new DirectionlessFrametime([100, 200, 80, 100])),
			WALK: new Activity("walk", new DirectionlessFrametime(new Array(8).fill(100))),
			WATER: new Activity("water", new DirectionlessFrametime([300, 600]))
		},
		values: new Array<Activity>()
	}

	static {
		Activity.enum.values = Object.values(Activity.enum.map);
	}

}

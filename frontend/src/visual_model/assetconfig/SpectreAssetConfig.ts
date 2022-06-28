export default class SpectreActivity {

	readonly ordinal: number;
	readonly id: string;
	private static nextOrdinal: number = 0;
	readonly frameCount: Array<number>
	readonly animTime: number
	readonly rowModifier: number

	private constructor(id: string, animTime: number, rowModifier: number, frametimes: Array<number>) {
		this.id = id;
		this.ordinal = SpectreActivity.nextOrdinal++;
		this.frameCount = frametimes;
		this.animTime = animTime;
		this.rowModifier = rowModifier;
	}

	static readonly enum = {
		map: {
			SWORD: new SpectreActivity("sword", 500, 0, [6, 6, 8, 8]),
			WALK: new SpectreActivity("walk", 750, 1, [4,8,4,4]),
		},
		values: new Array<SpectreActivity>()
	}

	static {
		SpectreActivity.enum.values = Object.values(SpectreActivity.enum.map);
	}

}
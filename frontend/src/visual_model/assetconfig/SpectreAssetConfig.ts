export default class SpectreActivity {

	readonly ordinal: number;
	readonly id: string;
	private static nextOrdinal: number = 0;
	readonly frameCount: number
	readonly animTime: number
	readonly rowModifier: number

	private constructor(id: string, frameCount: number, animTime: number, rowModifier: number) {
		this.id = id;
		this.ordinal = SpectreActivity.nextOrdinal++;
		this.frameCount = frameCount;
		this.animTime = animTime;
		this.rowModifier = rowModifier;
	}

	static readonly enum = {
		map: {
			SWORD: new SpectreActivity("sword", 8, 500, 0),
			WALK: new SpectreActivity("walk", 4, 750, 1),
		},
		values: new Array<SpectreActivity>()
	}

	static {
		SpectreActivity.enum.values = Object.values(SpectreActivity.enum.map);
	}

}
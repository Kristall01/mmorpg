export default class SkeletonActivity {

	readonly ordinal: number;
	readonly id: string;
	private static nextOrdinal: number = 0;
	readonly frameCount: number
	readonly animTime: number
	readonly rowModifier: number

	private constructor(id: string, frameCount: number, animTime: number, rowModifier: number) {
		this.id = id;
		this.ordinal = SkeletonActivity.nextOrdinal++;
		this.frameCount = frameCount;
		this.animTime = animTime;
		this.rowModifier = rowModifier;
	}

	static readonly enum = {
		map: {
			SWORD: new SkeletonActivity("sword", 3, 500, 0),
			WALK: new SkeletonActivity("walk", 4, 1000, 1),
		},
		values: new Array<SkeletonActivity>()
	}

	static {
		SkeletonActivity.enum.values = Object.values(SkeletonActivity.enum.map);
	}

}
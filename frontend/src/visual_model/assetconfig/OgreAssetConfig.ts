export default class OgreActivity {

	readonly ordinal: number;
	readonly id: string;
	private static nextOrdinal: number = 0;
	readonly frameCount: number
	readonly animTime: number
	readonly rowModifier: number

	private constructor(id: string, frameCount: number, animTime: number, rowModifier: number) {
		this.id = id;
		this.ordinal = OgreActivity.nextOrdinal++;
		this.frameCount = frameCount;
		this.animTime = animTime;
		this.rowModifier = rowModifier;
	}

	static readonly enum = {
		map: {
			SWORD: new OgreActivity("sword", 3, 500, 0),
			WALK: new OgreActivity("walk", 6, 750, 1),
		},
		values: new Array<OgreActivity>()
	}

	static {
		OgreActivity.enum.values = Object.values(OgreActivity.enum.map);
	}

}
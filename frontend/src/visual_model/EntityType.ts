/* class EntityType {

	constructor(id: number) {
		this.id = id;
	}

	readonly id: number

	static readonly enum = {
		HUMAN: new EntityType(0),
		UNKNOWN: new EntityType(1)
	}

} */

export class EntityType {

	readonly ordinal: number;
	readonly height: number
	private static nextOrdinal: number = 0;

	private constructor(height: number) {
		this.height = height;
		this.ordinal = EntityType.nextOrdinal++;
	}

	static readonly enum = {
		map: {
			HUMAN: new EntityType(0.6),
			UNKNOWN: new EntityType(1.1),
			SLIME: new EntityType(0.75),
			SKELETON: new EntityType(0.5),
			OGRE: new EntityType(0.5),
			DUMMY: new EntityType(1.0)
		},
		values: new Array<EntityType>()
	}

	static {
		EntityType.enum.values = Object.values(EntityType.enum.map);
	}

}

export default EntityType;
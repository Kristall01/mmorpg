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
	private static nextOrdinal: number = 0;

	private constructor() {
		this.ordinal = EntityType.nextOrdinal++;
	}

	static readonly enum = {
		map: {
			HUMAN: new EntityType(),
			UNKNOWN: new EntityType(),
		},
		values: new Array<EntityType>()
	}

	static {
		EntityType.enum.values = Object.values(EntityType.enum.map);
	}

}

export default EntityType;
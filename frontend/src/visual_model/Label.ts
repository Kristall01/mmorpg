import Entity from "./Entity";

export class LabelType {

	readonly code: number;
	readonly id: string;

	private constructor(id: string, code: number) {
		this.id = id;
		this.code = code;
	}

	static readonly enum = {
		map: {
			DAMAGE: new LabelType('DAMAGE', 0),
			HEAL: new LabelType('HEAL', 1),
		},
		values: new Array<LabelType>()
	}

	static {
		LabelType.enum.values = [LabelType.enum.map.DAMAGE, LabelType.enum.map.HEAL];
	}

}

export class WorldLabel {

	readonly text: string;
	readonly type: LabelType;
	readonly entity: Entity;
	readonly startTime: number;

	constructor(text: string, type: LabelType, entity: Entity, startTime?: number) {
		this.text = text;
		this.type = type;
		this.entity = entity;

		if(startTime === undefined) {
			this.startTime = performance.now();
		}
		else {
			this.startTime = startTime;
		}

	}

}
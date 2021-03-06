import { Position } from "./VisualModel";

const maxInterpolationDist = 3;
const interpolationTimeMs = 100;

export class Direction {

	readonly ordinal: number;
	readonly modifier: readonly number[]

	private constructor(ordinal: number, modifier: number[]) {
		this.ordinal = ordinal;
		this.modifier = Object.freeze(modifier);
	}

	static readonly enum = {
		map: {
			SOUTH: new Direction(0, [0,1]),
			WEST: new Direction(3, [-1,0]),
			NORTH: new Direction(1, [0,-1]),
			EAST: new Direction(2, [1,0]),
		},
		values: new Array<Direction>()
	}

	static {
		Direction.enum.values = Object.values(Direction.enum.map);
	}

}

export interface Status {
	position: Position,
	moving: boolean,
	facing: Direction
}

export type Path = {
	positions: null | Position[],
	statusFn: StatusFn
}

export type StatusFn = (rendertime: number) => Status

const pointDistance = (pos0: Position, pos1: Position): number => {
	return Math.sqrt(Math.pow(pos0[0] - pos1[0], 2) + Math.pow(pos0[1] - pos1[1], 2));
}

export const ConstStatus = (l: Position, facing: Direction): StatusFn => {
	return (rendertime: number) => ({
		facing: facing,
		moving: false,
		position: l
	});
}

export const EntityConstStatus = (mode: DirectionMode, lastPosition: Position, l: Position, facing: Direction): StatusFn => {
	return fixInterpolation(mode, lastPosition, (rendertime: number) => ({
		facing: facing,
		moving: false,
		position: l
	}));
}

export const EntityLinearStatus = (mode: DirectionMode, lastPosition: Position, startTimeMs: number, from: Position, to: Position, cellsPerSec: number): StatusFn => {
	return fixInterpolation(mode, lastPosition, LinearStatus(startTimeMs, from, to, cellsPerSec));
}

export const LinearStatus = (startTimeMs: number, from: Position, to: Position, cellsPerSec: number): StatusFn => {

	let xDiff = to[0] - from[0];
	let yDiff = to[1] - from[1];
	
	let totalDistance = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
	let totalTime = totalDistance / cellsPerSec * 1000;
	let endTime = startTimeMs + totalTime;

	return (currentTimeMs: number): Status => {
		if(currentTimeMs > endTime) {
			return ({
				facing: Direction.enum.map.SOUTH,
				moving: false,
				position: to
			});
		}
		return ({
			facing: Direction.enum.map.SOUTH,
			moving: true,
			position: [
				from[0] + xDiff * ((currentTimeMs - startTimeMs) / totalTime),
				from[1] + yDiff * ((currentTimeMs - startTimeMs) / totalTime)
			]
		});
	}
}

export type DirectionMode = "1"|"2"|"4";

export const calculatedDirection = (mode: DirectionMode, x: number, y: number): Direction => {
	const {EAST, NORTH, SOUTH, WEST} = Direction.enum.map;
	if(mode === "1") {
		return SOUTH;
	}
	if(mode === "2") {
		if(x <= 0) {
			return WEST;
		}
		else {
			return EAST;
		}
	}

	//edge case
	if(x == 0 && y == 0) {
		return SOUTH;
	}
	
	let absX = Math.abs(x);
	let absY = Math.abs(y);


	if(absX*2 < absY) {
		//verticalAxisDirection
		return y >= 0 ? SOUTH : NORTH;
	}
	return x >= 0 ? EAST : WEST;
}

export const radiusDistance = (p0: Position, p1: Position) => {
	return Math.sqrt(Math.pow(p0[0] - p1[0], 2) + Math.pow(p0[1] - p1[1], 2));
}

export const facingFunction = (facing: Direction, fn: StatusFn): StatusFn => {
	return (rendertime: number) => {
		let val = fn(rendertime);
		return {
			facing: facing,
			moving: val.moving,
			position: val.position
		}
	}
}


export const zigzagStatus = (mode: DirectionMode, startTimeMs: number, points: Position[], cellsPerSec: number): StatusFn => {
	let timePoints: number[] = [];
	let totalDist = 0;
	let totalTime = 0;
	let i = 0;
	while(true) {
		let t = totalDist / cellsPerSec*1000;
		totalTime += t;
		timePoints.push(startTimeMs + t);
		if(i >= points.length-1) {
			break;
		}
		totalDist += pointDistance(points[i], points[i+1]);
		i++;
	}

	let index = 0;

	let lastPoint = points[points.length-1];

	let fnVariable: StatusFn = null!;
	let lastDirection: Direction = calculatedDirection(mode, points[1][0] - points[0][0], points[1][1] - points[0][1]);

	let runningFn = (currentTimeMs: number): Status => {

		let i = index;
		for(;i < timePoints.length-1; ++i) {
			if(currentTimeMs < timePoints[i+1]) {
				break;
			}
		}
		index = i;
		if(i === timePoints.length-1) {
			fnVariable = ConstStatus(lastPoint, lastDirection);
			return fnVariable(currentTimeMs);
		}

		let xDiff = points[i+1][0] - points[i][0];
		let yDiff = points[i+1][1] - points[i][1];

		let returnedPos: Position = [
			points[i][0] + xDiff * ((currentTimeMs - timePoints[i]) / (timePoints[i+1] - timePoints[i])),
			points[i][1] + yDiff * ((currentTimeMs - timePoints[i]) / (timePoints[i+1] - timePoints[i])),
		];

		lastDirection = calculatedDirection(mode, xDiff, yDiff);

		return ({
			moving: true,
			position: returnedPos,
			facing: lastDirection
		});

/* 		return [
			points[i][0] + xDiff * ((currentTimeMs - timePoints[i]) / (timePoints[i+1] - timePoints[i])),
			points[i][1] + yDiff * ((currentTimeMs - timePoints[i]) / (timePoints[i+1] - timePoints[i])),
		]
 */	}

	fnVariable = runningFn;

	return time => fnVariable(time);
};

export const entityZigzagStatus = (mode: DirectionMode, lastPosition: Position, startTimeMs: number, points: Position[], cellsPerSec: number): StatusFn => {
	return fixInterpolation(mode, lastPosition, zigzagStatus(mode, startTimeMs, points, cellsPerSec));
}

const line = (mode: DirectionMode, from: Position, to: Position, fromTime: number, totalTime: number): StatusFn => {
	let xDiff = to[0] - from[0];
	let yDiff = to[1] - from[1];

	return (currentTimeMs: number): Status => {
		return {
			facing: calculatedDirection(mode, xDiff, yDiff),
			moving: true,
			position: [
				from[0] + xDiff * ((currentTimeMs - fromTime) / totalTime),
				from[1] + yDiff * ((currentTimeMs - fromTime) / totalTime)
			]
		}
	}
}

const fixInterpolation = (mode: DirectionMode, startPosition: Position, primitiveFn: StatusFn): StatusFn => {
	let now = performance.now();
	let firstStatus = primitiveFn(now+interpolationTimeMs);

	let firstPosition = firstStatus.position;

	let positionsDist = Math.sqrt(Math.pow(startPosition[0] - firstPosition[0], 2) + Math.pow(startPosition[1] - firstPosition[1], 2));
	if(positionsDist > maxInterpolationDist) {
		return primitiveFn;
	}

	let fixFn = line(mode, startPosition, firstPosition, now, interpolationTimeMs);
	let lastTime = now+interpolationTimeMs;
	return (currentTimeMs: number): Status => {
		if(currentTimeMs < lastTime) {
			return fixFn(currentTimeMs);
		}
		else {
			return primitiveFn(currentTimeMs);
		}
	}
}

/* export const CirclePath = (center: Position, circleRadius: number) : PathFn => {
	return (currentTimeMs: number) => {
		let time = currentTimeMs/1000;
		return [
			center[0] + Math.cos(time)*circleRadius,
			center[1] + Math.sin(time)*circleRadius
		]
	}
} */
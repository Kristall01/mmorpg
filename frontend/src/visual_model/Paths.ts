import { Position } from "./VisualModel";

const maxInterpolationDist = 3;
const interpolationTimeMs = 100;

export type PathFn = (rendertime: number) => Position

const pointDistance = (pos0: Position, pos1: Position): number => {
	return Math.sqrt(Math.pow(pos0[0] - pos1[0], 2) + Math.pow(pos0[1] - pos1[1], 2));
}

export const ConstPath = (l: Position): PathFn => {
	return (rendertime: number) => l;
}

export const EntityConstPath = (lastPosition: Position, l: Position): PathFn => {
	return fixInterpolation(lastPosition, (rendertime: number) => l);
}

export const EntityLinearPath = (lastPosition: Position, startTimeMs: number, from: Position, to: Position, cellsPerSec: number): PathFn => {
	return fixInterpolation(lastPosition, LinearPath(startTimeMs, from, to, cellsPerSec));
}

export const LinearPath = (startTimeMs: number, from: Position, to: Position, cellsPerSec: number): PathFn => {

	let xDiff = to[0] - from[0];
	let yDiff = to[1] - from[1];
	
	let totalDistance = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
	let totalTime = totalDistance / cellsPerSec * 1000;
	let endTime = startTimeMs + totalTime;

	return (currentTimeMs: number): Position => {
		if(currentTimeMs > endTime) {
			return to;
		}
		return [
			from[0] + xDiff * ((currentTimeMs - startTimeMs) / totalTime),
			from[1] + yDiff * ((currentTimeMs - startTimeMs) / totalTime)
		]
	}
}

export const zigzagPath = (startTimeMs: number, points: Position[], cellsPerSec: number): PathFn => {
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

	let constFn = () => lastPoint;

	let fnVariable: PathFn = null!;

	let runningFn = (currentTimeMs: number): Position => {

		let i = index;
		for(;i < timePoints.length-1; ++i) {
			if(currentTimeMs < timePoints[i+1]) {
				break;
			}
		}
		index = i;
		if(i === timePoints.length-1) {
			fnVariable = constFn;
			return lastPoint;
		}

		let xDiff = points[i+1][0] - points[i][0];
		let yDiff = points[i+1][1] - points[i][1];

		return [
			points[i][0] + xDiff * ((currentTimeMs - timePoints[i]) / (timePoints[i+1] - timePoints[i])),
			points[i][1] + yDiff * ((currentTimeMs - timePoints[i]) / (timePoints[i+1] - timePoints[i])),
		]
	}

	fnVariable = runningFn;

	return time => fnVariable(time);
};

export const entityZigzagPath = (lastPosition: Position, startTimeMs: number, points: Position[], cellsPerSec: number): PathFn => {
	return fixInterpolation(lastPosition, zigzagPath(startTimeMs, points, cellsPerSec));
}

const line = (from: Position, to: Position, fromTime: number, totalTime: number): PathFn => {
	let xDiff = to[0] - from[0];
	let yDiff = to[1] - from[1];

	return (currentTimeMs: number) => {
		return [
			from[0] + xDiff * ((currentTimeMs - fromTime) / totalTime),
			from[1] + yDiff * ((currentTimeMs - fromTime) / totalTime)
		]
	}
}

const fixInterpolation = (startPosition: Position, primiteFn: PathFn): PathFn => {
	let now = performance.now();
	let firstPosition = primiteFn(now+interpolationTimeMs);

	let positionsDist = Math.sqrt(Math.pow(startPosition[0] - firstPosition[0], 2) + Math.pow(startPosition[1] - firstPosition[1], 2));
	if(positionsDist > maxInterpolationDist) {
		return primiteFn;
	}

	let fixFn = line(startPosition, firstPosition, now, interpolationTimeMs);
	let lastTime = now+interpolationTimeMs;
	return (currentTimeMs: number): Position => {
		if(currentTimeMs < lastTime) {
			return fixFn(currentTimeMs);
		}
		else {
			return primiteFn(currentTimeMs);
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
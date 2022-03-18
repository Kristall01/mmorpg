import React from "react";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";

export function linearMove(fromX: number, fromY: number, fromtime: number, toX: number, toY: number, toTime: number): (rendertime: number) => [number,number] {
	let timeDiff = toTime - fromtime;
	let xDiff = toX - fromX;
	let yDiff = toY - fromY;

	return (rendertime: number) => {
		if(rendertime >= toTime) {
			return [toX,toY];
		};
		let timeRatio = (rendertime-fromtime)/timeDiff;
		return [fromX + timeRatio*xDiff, fromY + timeRatio*yDiff];
	}
}

export const enumValueOf = <T>(a: T, key: string): T[keyof T] | null => {
	let et = a[key as keyof typeof a];
	if(et !== undefined) {
		return et;
	}
	return null;
}
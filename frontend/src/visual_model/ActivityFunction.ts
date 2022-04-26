import { Activity } from "./assetconfig/HumanAssetConfig";
import { Status } from "./Paths";

export interface ActivitySnapshot {
	activity: Activity,
	animationTime: number
}

type ActivityFunction = (time: number) => ActivitySnapshot;

export function createWalkFunction(statusFn: () => Status, currentTime: number = 0): ActivityFunction {
	const startTime = performance.now()-currentTime;
	return (rendertime: number) => {
		let animTime: number;
		if(statusFn().moving) {
			animTime = rendertime - startTime;
		}
		else {
			animTime = 0;
		}
		return {
			activity: Activity.enum.map.WALK,
			animationTime: animTime
		}
	}
}

export function createSwordFunction(): ActivityFunction {
	//480 = sword swing animation time
	const startTime = performance.now();

	const baseAnimTime = 480;
	const speedAnimTime = 400;
	let multiplier = baseAnimTime/speedAnimTime;

	return (rendertime: number) => {

		let diff = (rendertime - startTime);
		if(diff < speedAnimTime) {
			return {
				activity: Activity.enum.map.SWORD,
				animationTime: diff*multiplier
			}
		}
		else {
			return {
				activity: Activity.enum.map.SWORD,
				animationTime: 0
			}
		}
	}
}

export const idleFunction = () => {
	return {
		activity: Activity.enum.map.WALK,
		animationTime: 0
	}
}


export default ActivityFunction;
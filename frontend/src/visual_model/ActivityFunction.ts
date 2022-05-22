import { Status } from "./Paths";

export interface ActivitySnapshot<T> {
	activity: T,
	animationTime: number
}

type ActivityFunction<T> = (time: number) => ActivitySnapshot<T>;

export function createWalkFunction<T>(activityMap: {WALK: T}, statusFn: () => Status, currentTime: number = 0): ActivityFunction<T> {
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
			activity: activityMap.WALK,
			animationTime: animTime
		}
	}
}

export function createSwordFunction<T>(activityMap: {SWORD: T,WALK:T}): ActivityFunction<T> {
	//480 = sword swing animation time
	const startTime = performance.now();

	const baseAnimTime = 480;
	const speedAnimTime = 400;
	let multiplier = baseAnimTime/speedAnimTime;

	return (rendertime: number) => {
		let diff = (rendertime - startTime);
		if(diff < speedAnimTime) {
			return {
				activity: activityMap.SWORD,
				animationTime: diff*multiplier
			}
		}
		else {
			return {
				activity: activityMap.WALK,
				animationTime: 0
			}
		}
	}
}

export const createIdleFunction = <T>(activityMap: {WALK: T}): ActivityFunction<T> => {
	return () =>({
		activity: activityMap.WALK,
		animationTime: 0
	});
}


export default ActivityFunction;
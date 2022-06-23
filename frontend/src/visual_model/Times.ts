export type TimeFunction = (now: number) => number

export const linearFunction = (from: number, to: number) => {
	const timespan = to - from;
	return (current: number) => {
		if(current >= to) {
			return 1;
		}
		else if(current <= from) {
			return 0;
		}
		return (current - from) / timespan;
	}
}

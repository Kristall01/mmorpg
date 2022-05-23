import UpdateBroadcaster from "visual_model/UpdateBroadcaster";

interface Subscription {
	broadcaster: UpdateBroadcaster<any>
	listener: EventListener
}

export default class SubManager {

	private subs: Array<Subscription> = [];

	subscribe<T>(broadcaster: UpdateBroadcaster<T>, baseListener: (event: T) => void) {
		let listener = broadcaster.addUpdateListener(baseListener);
		this.subs.push({broadcaster, listener});
	}

	removeAll() {
		for(let {broadcaster, listener} of this.subs) {
			broadcaster.removeUpdateListener(listener);
		}
		this.subs = [];
	}

}

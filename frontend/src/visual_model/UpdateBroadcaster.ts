const modelUpdateEventType = "model-update";

export default class UpdateBroadcaster<T> extends EventTarget {

	public triggerUpdate(type: T) {
		this.dispatchEvent(new CustomEvent(modelUpdateEventType, {detail: type}));
	}

	addUpdateListener(listener: (type: T) => void): EventListener {
		let changedListener = (e: any) => listener(e.detail);
		this.addEventListener(modelUpdateEventType, changedListener);
		return changedListener;
	}

	removeUpdateListener(t: EventListener | null) {
		this.removeEventListener(modelUpdateEventType, t);
	}

}
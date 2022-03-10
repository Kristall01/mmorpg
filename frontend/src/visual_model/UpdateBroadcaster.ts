const modelUpdateEventType = "model-update";

export default class UpdateBroadcaster<T> extends EventTarget {

	public triggerUpdate(type: T) {
		this.dispatchEvent(new CustomEvent(modelUpdateEventType, {detail: type}));
	}

	addUpdateListener(listener: (type: T) => void) {
		this.addEventListener(modelUpdateEventType, (e: any) => listener(e.detail));
	}

}
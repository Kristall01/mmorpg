const modelUpdateEventType = "model-update";

export default class UpdateBroadcaster extends EventTarget {

	public triggerUpdate(type: string) {
		this.dispatchEvent(new CustomEvent(modelUpdateEventType, {detail: type}));
	}

	addUpdateListener(listener: (type: string) => void) {
		this.addEventListener(modelUpdateEventType, (e: any) => listener(e.detail));
	}

}
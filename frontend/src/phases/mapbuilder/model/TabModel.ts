import UpdateBroadcaster from "visual_model/UpdateBroadcaster";
import TabManager from "./TabManager";

export default class TabModel extends UpdateBroadcaster<"close">{

	public readonly name: string
	public readonly id: number
	public readonly component: JSX.Element
	public readonly manager: TabManager
	
	constructor(manager: TabManager, id: number, name: string, component: (t: TabModel) => JSX.Element) {
		super();
		this.manager = manager;
		this.name = name;
		this.id = id;
		this.component = component(this);
	}

	close() {
		this.manager.closeTab(this.id);
	}

	select() {
		this.manager.selectTab(this.id);
	}

	isActive() {
		return this.manager.getActiveTab()?.id === this.id;
	}

}
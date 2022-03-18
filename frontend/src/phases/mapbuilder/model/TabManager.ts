import UpdateBroadcaster from "visual_model/UpdateBroadcaster";
import MapbuildModel from "./MapbuildModel";
import TabModel from "./TabModel";

export type Events = "added" | "activate" | "removed";

export default class TabManager extends UpdateBroadcaster<Events> {

	private model: MapbuildModel
	private tabMap: Map<number, TabModel> = new Map()
	private nextID: number = 0
	private activeTab: TabModel | null = null

	constructor(model: MapbuildModel) {
		super();
		this.model = model;
	}

	addTab(label: string, content: (t: TabModel) => JSX.Element): TabModel {
		let id = this.nextID++;
		let t = new TabModel(this, id, label, content);
		this.tabMap.set(id, t);
		this.triggerUpdate("added");
		this.selectTab(id);
		return t;
	}

	selectTab(tabID: number | null) {
		if(tabID === null) {
			this.activeTab = null;
		}
		else {
			this.activeTab = tabID == null ? null : (this.tabMap.get(tabID) ?? null);
		}
		this.triggerUpdate("activate");
	}

	closeTab(tabID: number) {
		if(!this.tabMap.delete(tabID)) {
			return;
		}
		this.triggerUpdate("removed");
		if(this.activeTab !== null && this.activeTab.id === tabID) {
			if(this.tabMap.size === 0) {
				this.selectTab(null);
			}
			else {
				this.selectTab(Object.values(Object.fromEntries(this.tabMap.entries()))[0].id);
			}
		}
	}

	getActiveTab(): TabModel | null {
		return this.activeTab;
	}

	getTabs(): Iterable<TabModel> {
		return this.tabMap.values();
	}

}
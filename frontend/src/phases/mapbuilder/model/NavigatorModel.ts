import UpdateBroadcaster from "visual_model/UpdateBroadcaster";
import CustomNavigationOption from "./navoptions/CustomNavigationOption";
import { MenuNavigationOption } from "./navoptions/MenuNavigationOption";
import { NavigationOption } from "./navoptions/NavigationOption";

export type Events = "switch" | "add" | "element" | "option" | "exit";

/* interface NavigationOptionElement {
	opt: NavigationOption
	id: number
} */

export default class NavigatorModel extends UpdateBroadcaster<Events> {

	private map: Map<number, NavigationOption> = new Map<number, NavigationOption>();
	private nextOptionID: number = 1;

	private activeOption: NavigationOption | null = null
	private element: JSX.Element | null = null

	public readonly exitOption: NavigationOption

	constructor() {
		super();
		this.exitOption = new CustomNavigationOption(0, this, "fa-solid fa-person-to-door", "exit", () => {
			this.triggerUpdate("exit");
		})
	}

	getActiveElement(): JSX.Element | null{
		return this.element;
	}

	setActiveElement(element: JSX.Element | null) {
		this.element = element;
	}

	createMenuOption(icon: string, label: string, elementGenerator: () => JSX.Element): NavigationOption {
		let ID = this.nextOptionID++;
		let opt = new MenuNavigationOption(ID, this, icon, label, elementGenerator);
		this.addOption(opt);
		return opt;
	}

	createCustomOption(icon: string, label: string, actionHandler: () => void): NavigationOption {
		let ID = this.nextOptionID++;
		let opt = new CustomNavigationOption(ID, this, icon, label, actionHandler);
		this.addOption(opt);
		return opt;
	}

	getOption(id: number): NavigationOption | null {
		return this.map.get(id) ?? null;
	}

	private addOption(opt: NavigationOption) {
		this.map.set(opt.id, opt);
		this.triggerUpdate("add");
	}

/* 	getActiveElement() {
		return this.activeElement;
	} */

/* 	setActiveElement(e: JSX.Element | null) {
		this.activeElement = e;
		this.triggerUpdate("element");
	} */

	setActiveOption(optionID: number | null) {
		if(optionID === null) {
			this.activeOption = null;
		}
		else {
			this.activeOption = this.map.get(optionID) ?? null;
		}
		this.triggerUpdate("option");
	}

	getActiveOption() {
		return this.activeOption;
	}

	getOptions(): Iterable<NavigationOption> {
		return this.map.values();
	}

}
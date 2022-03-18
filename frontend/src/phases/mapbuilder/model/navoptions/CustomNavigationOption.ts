import NavigatorModel from "../NavigatorModel";
import { NavigationOption } from "./NavigationOption"

export default class CustomNavigationOption extends NavigationOption {

	private action: () => void

	constructor(id: number, nav: NavigatorModel, icon: string, label: string, handleInteract: () => void) {
		super(id, nav,icon,label);
		this.action = handleInteract;
	}

	public handleInteract(): void {
		this.action();
	}

}
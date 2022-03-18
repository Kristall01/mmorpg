import NavigatorModel from "../NavigatorModel";
import { NavigationOption } from "./NavigationOption";

export class MenuNavigationOption extends NavigationOption {

	private elementGenerator: () => JSX.Element

	constructor(id: number, nav: NavigatorModel, icon: string, label: string, elementGenerator: () => JSX.Element) {
		super(id, nav, icon, label);
		this.elementGenerator = elementGenerator;
	}

	public handleInteract(): void {
		let activeOption = this.nav.getActiveOption();
		if(activeOption !== null && activeOption.id === this.id) {
			this.nav.setActiveOption(null);
			this.nav.setActiveElement(null);
		}
		else {
			this.nav.setActiveOption(this.id);
			this.nav.setActiveElement(this.elementGenerator());
		}
	}

}
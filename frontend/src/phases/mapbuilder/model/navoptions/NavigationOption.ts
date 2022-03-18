import UpdateBroadcaster from "visual_model/UpdateBroadcaster";
import NavigatorModel from "../NavigatorModel";

export interface OtpionConfig {
	icon?: string,
	label?: string
}

export abstract class NavigationOption extends UpdateBroadcaster<"look"> {

	private _icon: string
	private _label: string
	
	public readonly id: number
	public readonly nav: NavigatorModel

	public abstract handleInteract(): void;

	constructor(id: number, nav: NavigatorModel, icon: string, label: string, ) {
		super();
		this.id = id;
		this._icon = icon;
		this._label = label;
		this.nav = nav;
	}

	setConfig(config: OtpionConfig) {
		let anyChange = false;
		if(config.icon !== undefined) {
			this._icon = config.icon;
			anyChange = true;
		}
		if(config.label !== undefined) {
			this._label = config.label;
			anyChange = true;
		}
		if(anyChange) {
			this.triggerUpdate("look");
		}
	}

	get icon(): string {
		return this._icon;
	}

	get label(): string {
		return this._label;
	}

}
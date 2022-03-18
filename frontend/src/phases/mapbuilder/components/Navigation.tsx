import { Component, useContext, useEffect, useState } from "react"
import { MapbuildModelContext } from "../MapBuilder"
import { NavigationOption } from "../model/navoptions/NavigationOption"
import NavigatorModel, { Events } from "../model/NavigatorModel";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";
import NavigationOptionView from "./NavigationOptionView";
import SubManager from "SubManager";

export type props = {
	nav: NavigatorModel
}

class Navigation extends Component<props> {

	private subs: SubManager

	constructor(props: props) {
		super(props);
		this.subs = new SubManager();
	}

	componentDidMount() {
		this.subs.subscribe(this.props.nav, () => this.forceUpdate());
	}

	componentWillUnmount() {
		this.subs.removeAll();
	}

	render() {
		let divs: Array<JSX.Element> = [];

		let nav = this.props.nav;

		let options = nav.getOptions();
		let active = nav.getActiveOption();

		for(let opt of options) {
			let {handleInteract, icon, id, label, nav} = opt;
			let c = ["nav-option"];
			if(active?.id === opt.id) {
				c.push("active");
			}
			let a = <NavigationOptionView key={opt.id} opt={opt} active={active?.id === opt.id} onInteract={opt.handleInteract.bind(opt)} />;
			divs.push(a);
			//<div title={label} className={c.join(" ")} onClick={handleInteract.bind(opt)} key={id}><i className={icon} /></div>);
			//handleInteract.bind(opt)
			//key={id}
		}
		let e = nav.getActiveElement();
		let contentDiv: JSX.Element | null = null;
		if(e !== null) {
			contentDiv = <div className="content">{e}</div>;
		}
		return (
			<div className="navigation-component">
				<div className="optionstrip">
					<div className="option-group">
						{divs}
					</div>
					<div className="option-group">
						<NavigationOptionView onInteract={nav.exitOption.handleInteract.bind(nav.exitOption)} opt={nav.exitOption} />
					</div>
				</div>
				{contentDiv}
			</div>
		)
	}

};

export default Navigation;
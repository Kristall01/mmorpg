import React, { useContext, useState } from "react"
import { MapbuildModelContext } from "./MapBuilder"
import MapbuildModel from "./MapbuildModel"

export interface NavigationOption {
	interact?: ((model: MapbuildModel) => void)
	element?: () => JSX.Element
	icon: string,
	id: string,
	label?: string
}

export type props = {
	options: NavigationOption[]
}

const Navigation = ({options}: props) => {

	let divs = [];

	let [content, setContent] = useState<NavigationOption | null>(null);

	let model = useContext(MapbuildModelContext);

	const changeContent = (e: NavigationOption) => {
		if(content === null || content.id !== e.id) {
			setContent(e);
		}
		else {
			setContent(null);
		}
	}

	
	for(let i = 0; i < options.length; ++i) {
		let {interact, icon, id, label, element} = options[i]
		let c = ["nav-option"];
		if(content?.id === id) {
			c.push("active");
		}
		let clickHandlers: Array<() => void> = [];
		if(interact !== undefined) {
			clickHandlers.push(() => interact!(model));
		}
		if(element !== undefined) {
			clickHandlers.push(() => changeContent(options[i]));
		}
		divs.push(<div title={label} className={c.join(" ")} onClick={() => {clickHandlers.forEach(c => c())}} key={id}>
				<i className={icon}></i>
			</div>);
	}
	let e = content?.element;
	let contentDiv = content === null || e === undefined ? null : <div className="content">{e()}</div>
	return (
		<div className="navigation-component">
			<div className="options">
				{divs}
			</div>
			{contentDiv}
		</div>
	)

};

export default Navigation;
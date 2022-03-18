import { Component, ReactChild, ReactElement, ReactNode } from 'react';
import SubManager from 'SubManager';
import { NavigationOption } from '../model/navoptions/NavigationOption';

export type props = {
	active?: boolean
	onInteract: () => void
	opt: NavigationOption
}

export default class NavigationOptionView extends Component<props> {

	public readonly subManager: SubManager = new SubManager();

	constructor(props: props) {
		super(props);
	}

	componentDidMount() {
		this.subManager.subscribe(this.props.opt, () => this.forceUpdate());
	}

	componentWillUnmount() {
		this.subManager.removeAll();
	}

	render() {
		let {onInteract, active, opt} = this.props;
		let c = ["nav-option"];
		if(this.props.active) {
			c.push("active");
		}
		return (
			<div title={opt.label} className={c.join(" ")} onClick={onInteract} >
				<i className={opt.icon} />
			</div>
		);
	}

}
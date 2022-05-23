import VisualResources from "game/VisualResources";
import LogicModel from "model/LogicModel";
import { Component } from "react";
import VisualModel, { UpdateTypes } from "visual_model/VisualModel";
import World from "visual_model/World";
import WorldView from "./WorldView";
import "./WorldView.scss";

export type props = {
	logicModel: LogicModel
	visualModel: VisualModel,
	visuals: VisualResources
}

export type state = {
	world: World | null
}

class ConditionalWorldView extends Component<props, state> {

	private logicModel: LogicModel
	private visualModel: VisualModel
	private visuals: VisualResources

	private mounted: boolean = false;
	private updateListener: EventListener | null = null;

	constructor(props: props) {
		super(props);
		let {visualModel, logicModel, visuals} = props;
		this.visualModel = visualModel;
		this.logicModel = logicModel;
		this.visuals = visuals

		this.state = {
			world: null
		}
	}

	async componentDidMount() {
		this.mounted = true;
		this.updateListener = this.props.visualModel.addUpdateListener(e => this.handleUpdate(e));
	}

	componentWillUnmount() {
		if(this.updateListener !== null) {
			this.props.visualModel.removeUpdateListener(this.updateListener);
		}
	}

	private handleUpdate(t: UpdateTypes) {
		if(t === "world") {
			this.setState({world: this.visualModel.world});
			return;
		}
	}

	render() {
		if(this.state.world !== null) {
			return (
				<WorldView
					logicModel={this.props.logicModel}
					visualModel={this.visualModel}
					world={this.state.world}
					visuals={this.props.visuals}
				/>
			);
		}
		else {
			return <div className="empty"></div>
		}
	}

};

export default ConditionalWorldView;
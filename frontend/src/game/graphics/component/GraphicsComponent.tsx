import { Component } from "react";
import Renderable from "../Renderable";
import RenderScheduler from "../RenderScheduler";
import "./GraphicsComponent.scss";

type props = {
	renderable: Renderable,
	maxFPS?: number | null,
	showFpsCounter?: boolean
}

const fillFpsCounterValue = (a: boolean | undefined) => {
	return a ?? false;
}


class GraphicsComponent extends Component<props> {

	private scheduler: RenderScheduler | null = null
	//private mixer: LayerMixer
	//private worldView: WorldView
	private maxFPS: number | null = null;
	private renderable: Renderable

	constructor(props: props) {
		super(props);
		let {maxFPS = null, renderable, showFpsCounter} = props;
		this.renderable = renderable;
		this.maxFPS = maxFPS;

		/* let model = props.model;
		this.worldView = props.view; */

		//this.mixer = new LayerMixer();
		//this.mixer.addLayer(this.worldView);
	}

	shouldComponentUpdate(nextProps: Readonly<props>, nextState: Readonly<{}>, nextContext: any) {
		let maxfpsCandidate = nextProps.maxFPS;
		if(maxfpsCandidate === undefined) {
			return false;
		}
		if(maxfpsCandidate !== this.maxFPS) {
			this.maxFPS = maxfpsCandidate;
			this.scheduler?.setMaxFps(maxfpsCandidate);
		}
		if(nextProps.renderable !== this.renderable) {
			this.renderable = nextProps.renderable;
			this.scheduler?.setScene(this.renderable);
		}
		if(nextProps.showFpsCounter !== this.props.showFpsCounter) {
			this.scheduler?.setFpsCounterVisible(fillFpsCounterValue(nextProps.showFpsCounter));
		}
		return false;
	}

	private setScheduler(parent: HTMLElement | null) {
		if(this.scheduler !== null) {
			this.scheduler.setScene(null);
			this.scheduler = null;
		}
		if(parent !== null) {
			this.scheduler = new RenderScheduler(parent);
			this.scheduler.setScene(this.renderable);
			this.scheduler.setMaxFps(this.maxFPS);
			this.scheduler.setFpsCounterVisible(fillFpsCounterValue(this.props.showFpsCounter));
		}
	}

	componentWillUnmount() {
		if(this.scheduler !== null) {
			this.scheduler.setScene(null);
		}
	}

	render() {
		return <div ref={parent => this.setScheduler(parent)} className="canvas-parent"></div>
	}

}

export default GraphicsComponent;
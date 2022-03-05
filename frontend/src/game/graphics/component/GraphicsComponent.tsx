import { Component } from "react";
import "./GraphicsComponent.scss";
import VisualModel from "visual_model/VisualModel";
import RenderScheduler from "../RenderScheduler";
import LayerMixer from "../RenderableCombinator";
import WorldView from "../worldview/WorldView";
import Renderable from "../Renderable";

type props = {
	renderable: Renderable,
	maxFPS?: number | null
}

class GraphicsComponent extends Component<props> {

	private scheduler: RenderScheduler | null = null
	//private mixer: LayerMixer
	//private worldView: WorldView
	private maxFPS: number | null = null;
	private renderable: Renderable

	constructor(props: props) {
		super(props);
		let {maxFPS = null, renderable} = props;
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
		}
	}

	render() {
		return <div ref={parent => this.setScheduler(parent)} className="canvas-parent"></div>
	}

}

export default GraphicsComponent;
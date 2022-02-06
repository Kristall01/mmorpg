import { Component } from "react";
import "./GraphicsComponent.scss";
import VisualModel from "visual_model/VisualModel";
import RenderScheduler from "../RenderScheduler";
import LayerMixer from "../RenderableCombinator";
import WorldView from "../layers/WorldView";

type props = {
	model: VisualModel,
	view: WorldView
}

class GraphicsComponent extends Component<props> {

	private scheduler: RenderScheduler | null = null
	private mixer: LayerMixer
	private worldView: WorldView

	constructor(props: props) {
		super(props);

		let model = props.model;
		this.worldView = props.view;

		this.mixer = new LayerMixer();
		this.mixer.addLayer(this.worldView);
	}

	shouldComponentUpdate() {
		return false;
	}

	private setScheduler(parent: HTMLElement | null) {
		if(this.scheduler !== null) {
			this.scheduler.setScene(null);
			this.scheduler = null;
		}
		if(parent !== null) {
			this.scheduler = new RenderScheduler(parent);
			this.scheduler.setScene(this.mixer);
			this.scheduler.setMaxFps(null);
		}
	}

	render() {
		return <div ref={parent => this.setScheduler(parent)} className="canvas-parent"></div>
	}

}

export default GraphicsComponent;
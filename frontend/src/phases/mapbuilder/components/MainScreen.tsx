import React from "react";
import MapbuildModel, { MapbuildEvents } from "../model/MapbuildModel";
import ConnectedComponent from "../../../ConnectedComponent";
import TextureGrid from "./TextureGrid";
import ProjectModel, { ProjectEvents } from "../model/ProjectModel";

type props = {
	model: MapbuildModel
}

export default class MainScreen extends ConnectedComponent<MapbuildEvents, props, {}> {

	constructor(props: props) {
		super(props, [props.model]);
	}

	handleEvent(t: MapbuildEvents) {
		if(t === "world-select") {
			this.forceUpdate();
		}
	}

	render(): JSX.Element {
		let level = this.props.model.getActiveLevel();
		if(level === null) {
			return <div>¯\_(ツ)_/¯</div>
		}
//		return <div></div>
		return <TextureGrid grid={p.getLevels()} cellSize={0} />
	}

}
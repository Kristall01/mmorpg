import React from "react";
import MapbuildModel, { MapbuildEvents } from "../model/MapbuildModel";
import ConnectedComponent from "../../../ConnectedComponent";
import ProjectModel, { ProjectEvents } from "../model/ProjectModel";
import TextureGrid from "./TextureGrid";

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
		return (
			<TextureGrid level={level} cellsize={50} />
		)
	}

}
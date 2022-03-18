import React from "react";
import MapbuildModel from "../model/MapbuildModel";
import ConnectedComponent from "../../../ConnectedComponent";
import TextureGrid from "./TextureGrid";

type props = {
	model: MapbuildModel
}

export default class MainScreen extends ConnectedComponent<props, {}> {

	constructor(props: props) {
		super(props, [props.model]);
	}

	render(): JSX.Element {
		let p = this.props.model.getProject();
		return <div></div>
//		return <TileGrid grid={p.getLevels()} cellSize={0} />
	}

}
import ProjectModel from "phases/mapbuilder/model/ProjectModel";
import { ReactNode } from "react";
import ConnectedComponent from "../../../../ConnectedComponent";

export type LayerProps = {
	project: ProjectModel
}

export default class Layers extends ConnectedComponent<LayerProps> {

	constructor(props: LayerProps) {
		super(props, [props.project]);
	}

	render(): ReactNode {
		return <div>asd</div>
	}
	
}

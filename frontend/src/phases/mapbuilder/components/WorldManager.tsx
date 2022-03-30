import ConnectedComponent from "ConnectedComponent"
import { ReactNode } from "react";
import ProjectModel, { ProjectEvents } from "../model/ProjectModel";

export interface Props {
	project: ProjectModel
}

export default class WorldManager extends ConnectedComponent<ProjectEvents, Props, {}> {

	constructor(props: Props) {
		super(props, [props.project]);
	}

	render(): React.ReactNode {
		return <div></div>
	}

}
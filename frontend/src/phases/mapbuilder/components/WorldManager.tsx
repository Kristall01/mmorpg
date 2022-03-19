import ConnectedComponent from "ConnectedComponent"
import { ReactNode } from "react";
import ProjectModel from "../model/ProjectModel";

export interface Props {
	project: ProjectModel
}

export default class WorldManager extends ConnectedComponent<Props, {}> {

	constructor(props: Props) {
		super(props, [props.project]);
	}

	render(): JSX.Element {
		return <div></div>
	}

}
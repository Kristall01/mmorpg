import ConnectedComponent from "ConnectedComponent"
import React, { ReactNode } from "react";
import MapbuildModel from "../model/MapbuildModel";
import ProjectModel, { ProjectEvents } from "../model/ProjectModel";

export interface Props {
	project: ProjectModel
	model: MapbuildModel
}

export default class WorldManager extends ConnectedComponent<ProjectEvents, Props, {}> {

	constructor(props: Props) {
		super(props, [props.project]);
	}

	handleAddWorldClick(e: React.MouseEvent) {
		let w = prompt("világ neve:");
		if(w === null) {
			return;
		}
		this.props.project.addLevel(w);
	}

	render(): React.ReactNode {
		let proj = this.props.project;
		let worlds = Array.from(proj.getLevels());
		let worldList: ReactNode
		if(worlds.length === 0) {
			worldList = <div>Még nem hoztál létre világot</div>;
		}
		else {
			worldList = (
				<ul>
					{
						worlds.map((a, i) => {
							return (
								<li key={i} className="world-label">
									{a[0]}
									<button onClick={() => this.props.model.activateLevel(a[0])}>activate</button>
								</li>
							)
						})
					}
				</ul>
			)
		}
		return (
			<>
				<div className="buttonlist">
					<div onClick={e => this.handleAddWorldClick(e)} className="button">új világ létrehozása</div>
				</div>
				{worldList}
			</>
		)
	}

}
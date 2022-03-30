import MapbuildModel from "phases/mapbuilder/model/MapbuildModel";
import ProjectModel, { ProjectEvents } from "phases/mapbuilder/model/ProjectModel";
import { ReactNode } from "react";
import Level, { LevelEvents } from "visual_model/Level";
import ConnectedComponent from "../../../../ConnectedComponent";

export type LayerProps = {
	level: Level
}

class Layers extends ConnectedComponent<LevelEvents, LayerProps> {

	constructor(props: LayerProps) {
		super(props, [props.level])
	}

	render() {
		return (
			<ul>
				{Array.from(this.props.level.getLayers()).map((a,b) => <li key={b}>layer #{a[0]}</li>)}
			</ul>
		)
	}

}


export type LayerManagerProps = {
	project: ProjectModel
	model: MapbuildModel
}

export default class LayerManager extends ConnectedComponent<ProjectEvents, LayerManagerProps> {

	constructor(props: LayerManagerProps) {
		super(props, [props.project]);
	}

	render(): ReactNode {
		let level = this.props.model.getActiveLevel();
		if(level === null) {
			return (<div>Először ki kell választani egy világot a rétegek szerkesztéséhez</div>);
		}
		return (
			<>
				<div className="buttonlist">
					<div className="button" onClick={() => level!.addLayer()}>új réteg hozzáadása</div>
				</div>
				<Layers level={level} />
			</>
		)
	}
	
}

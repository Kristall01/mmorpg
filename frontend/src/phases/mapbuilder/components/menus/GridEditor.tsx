import ConnectedComponent from 'ConnectedComponent';
import { ModelEventType } from 'model/Definitions';
import MapbuildModel, { MapbuildEvents } from 'phases/mapbuilder/model/MapbuildModel';
import NamedLevel from 'phases/mapbuilder/model/NamedLevel';
import { expandBottom, expandLeft, expandRight, expandTop } from 'phases/mapbuilder/model/Resizer';
import { useContext } from 'react';
import Level from 'visual_model/Level';
import { MapbuildModelContext } from '../../MapBuilder';
import TextureGridModel from '../../model/TextureGridModel';

export type GridEditorProps = {
	model: MapbuildModel
}

class GridEditor extends ConnectedComponent<MapbuildEvents, GridEditorProps> {

	constructor(props: GridEditorProps) {
		super(props, [props.model]);
	}

	render() {
		let rawLevel = this.props.model.getActiveLevel();
		if(rawLevel === null) {
			return <div>no level to edit :/</div>;
		}
		let level: Level = rawLevel.level;
		const change = (l: Level) => {
			this.props.model.getProject().setLevel(rawLevel!.name, l);
			this.props.model.activateLevel(rawLevel!.name);
		}
		return (
			<div className="buttonlist">
				<div className="button" onClick={() => change(expandTop(1, level))}>expand top</div>
				<div className="button" onClick={() => change(expandBottom(1, level))}>expand Bottom</div>
				<div className="button" onClick={() => change(expandLeft(1, level))}>expand left</div>
				<div className="button" onClick={() => change(expandRight(1, level))}>expand right</div>
			</div>
		)
	}

};

export default GridEditor;
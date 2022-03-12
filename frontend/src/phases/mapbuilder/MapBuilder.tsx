import GameView from 'game/GameView';
import CozyPack from 'game/graphics/texture/CozyPack';
import TexturePack from 'game/graphics/texture/TexturePack';
import VisualResources from 'game/VisualResources';
import DModel from 'model/impl/demo/DModel';
import NetworkModel from 'model/impl/ws/NetworkModel';
import GameScene from 'phases/game/GameScene';
import React, { createContext, createRef, useEffect, useState } from 'react';
import './MapBuilder.scss';
import MapbuildModel from './MapbuildModel';
import Buttons from './menus/Buttons';
import GridEditor from './menus/GridEditor';
import TileBrowser from './menus/TileBrowser';
import Navigation, { NavigationOption } from './Navigation';
import TileGridGomponent from './TileGridComponent';

export const MapbuildModelContext = createContext<MapbuildModel>(null!);
export const VisualResourcesContext = createContext<VisualResources>(null!);

const navOptions: NavigationOption[] = [
	{
		element: () => <TileBrowser />,
		icon: "fa-solid fa-block-brick",
		id: "tiles",
		label: "tiles",

	},
	{
		element: () => <Buttons />,
		icon: "fa-solid fa-ellipsis",
		id: "buttons",
		label: "options"
	},
	{
		element: () => <GridEditor />,
		icon: "fa-solid fa-grid",
		id: "grid",
		label: "edit grid"
	},
	{
		interact: (model: MapbuildModel) => {model.toggleGame()},
		icon: "fa-solid fa-play",
		id: "play",
		label: "play"
	}
]

export type props = {
	visuals: VisualResources
}

class MapBuilder extends React.Component<props> {

	private model: MapbuildModel;

	constructor(props: props) {
		super(props);
		this.model = new MapbuildModel(() => this.forceUpdate());
	}

	onWheel(e: React.WheelEvent) {
		if(e.altKey) {
			let nextLevel = this.model.getWheel() * (1 - e.deltaY / 750);
			nextLevel = Math.max(nextLevel, 20);
			this.model.setWheel(nextLevel);
		}
	}

	render() {
		let grid = this.model.getGrid();
		let gridComponent = grid === null ? null : <TileGridGomponent grid={grid} cellSize={this.model.getWheel()} />

		let centerComponent = null;
		if(this.model.game && grid != null) {
			centerComponent = <GameScene disconnectHandler={() => {}} visuals={this.props.visuals} modelGenerator={a => new DModel(a, "TESZT", grid?.map(a => a?.name || "?"))} />
		}
		else {
			centerComponent = gridComponent;
		}

		return (
			<MapbuildModelContext.Provider value={this.model}>
				<VisualResourcesContext.Provider value={this.props.visuals}>
					<div className="map-builder-component">
						<div className="side">
							<Navigation options={navOptions}/>
						</div>
						<div className="center" onWheel={(e) => this.onWheel(e)}>
							{centerComponent}
						</div>
					</div>
				</VisualResourcesContext.Provider>
			</MapbuildModelContext.Provider>
		)
	}

};

export default MapBuilder;
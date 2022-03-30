import VisualResources from 'game/VisualResources';
import DModel from 'model/impl/demo/DModel';
import GameScene from 'phases/game/GameScene';
import React, { createContext } from 'react';
import './MapBuilder.scss';
import MapbuildModel, {MapbuildEvents} from './model/MapbuildModel';
import Buttons from './components/menus/Buttons';
import GridEditor from './components/menus/GridEditor';
import TileBrowser from './components/menus/TileBrowser';
import Navigation from './components/Navigation';
import TabExplorer from './components/tabs/TabExplorer';
import GameView from 'game/GameView';
import SubManager from 'SubManager';
import { NavigationOption } from './model/navoptions/NavigationOption';
import Persistence from './components/menus/Persistence';
import LayerManager from './components/menus/LayerManager';
import MenuContext from 'MenuContext';
import { LandingPhase } from "phases/landing/LandingPhase"
import { Events } from './model/NavigatorModel';
import MainScreen from './components/MainScreen';
import ProjectModel from './model/ProjectModel';
import WorldManager from "./components/WorldManager";

export const MapbuildModelContext = createContext<MapbuildModel>(null!);
export const VisualResourcesContext = createContext<VisualResources>(null!);

export type props = {
	poject: ProjectModel
}

/*

const navOptions: NavigationOption[] = [
	new NavigationOption()
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
]*/

class MapBuilder extends React.Component<props, {}, typeof MenuContext> {

	private model: MapbuildModel;
	private updateHandler: EventListener | null = null
	private subManager: SubManager = new SubManager();
	private gameNavOpt: NavigationOption

	constructor(props: props) {
		super(props);
		let model = new MapbuildModel(props.poject);
		let nav = model.navigator;
		nav.createMenuOption("fa-solid fa-block-brick", "tiles", () => <TileBrowser />);
		nav.createMenuOption("fa-solid fa-ellipsis", "options", () => <Buttons />);
		nav.createMenuOption("fa-solid fa-crop", "edit grid", () => <GridEditor />);
		nav.createMenuOption("fa-solid fa-layer-group", "manage layers", () => <LayerManager model={this.model} project={this.props.poject} />);
		nav.createMenuOption("fa-regular fa-map", "worlds", () => <WorldManager model={this.model} project={this.props.poject}/>)
		this.gameNavOpt = nav.createCustomOption("fa-solid fa-play", "start testing", () => {model.toggleGame()});
		//this.gridNavOpt = 
		this.model = model;
	}

	componentDidMount() {
		this.subManager.subscribe(this.model, this.handleUpdateEvent.bind(this));
		this.subManager.subscribe(this.model.navigator, this.handleNavigatorUpdate.bind(this));
//		this.updateHandler = this.model.addUpdateListener(this.handleUpdateEvent.bind(this));
	}

	componentWillUnmount() {
		this.subManager.removeAll();
	}

	private handleNavigatorUpdate(type: Events) {
		if(type === "exit") {
			this.context(() => <LandingPhase />);
		}
	}

	private handleUpdateEvent(type: MapbuildEvents) {
		if(type === "game") {
			if(this.model.isGameShown()) {
				this.gameNavOpt.setConfig({icon: "fa-solid fa-pause", "label": "stop testing"});
			}
			else {
				this.gameNavOpt.setConfig({icon: "fa-solid fa-play", "label": "start testing"});
			}
		}
	}

	onWheel(e: React.WheelEvent) {
		if(e.altKey) {
			let nextLevel = this.model.getWheel() * (1 - e.deltaY / 750);
			nextLevel = Math.max(nextLevel, 20);
			this.model.setWheel(nextLevel);
		}
	}

	render() {
		return (
			<MapbuildModelContext.Provider value={this.model}>
				<VisualResourcesContext.Provider value={/* this.props.visuals */null!}>
					<div className="map-builder-component">
						<div className="side">
							<Navigation nav={this.model.navigator} />
						</div>
						<div className="center" onWheel={(e) => this.onWheel(e)}>
							<MainScreen model={this.model}/>
						</div>
					</div>
				</VisualResourcesContext.Provider>
			</MapbuildModelContext.Provider>
		)
	}

};

MapBuilder.contextType = MenuContext;

export default MapBuilder;
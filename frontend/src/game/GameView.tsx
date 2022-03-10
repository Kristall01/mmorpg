import LogicModel from "model/LogicModel";
import React, { createContext, createRef } from "react";
import VisualModel, {focus, Position} from "visual_model/VisualModel";
import GraphicsComponent from "./graphics/component/GraphicsComponent";
import WorldRenderer from "./graphics/renderers/world/WorldRenderer";
import Chat from "./ui/chat/Chat";

import "./GameView.scss";
import CozyPack from "./graphics/texture/CozyPack";
import ImageStore from "./ImageStore";
import TexturePack from "./graphics/texture/TexturePack";
import WorldView from "./graphics/world/WorldView";
import ConditionalWorldView from "./graphics/world/ConditionalWorldView";
import VisualResources from "./VisualResources";

export type props = {
	logicModel: LogicModel
	visualModel: VisualModel,
	visuals: VisualResources
}

export type Models = [LogicModel, VisualModel];

export const ModelContext = createContext<Models>(null!);

export default class GameView extends React.Component<props, {}> {

	private mainRef = createRef<HTMLDivElement>();
	private visualModel: VisualModel
	private logicModel: LogicModel
	private visuals: VisualResources

	constructor(props: props) {
		super(props);

		let {logicModel, visualModel, visuals} = props;
		this.visualModel = visualModel;
		this.logicModel = logicModel;
		this.visualModel = visualModel;
		this.visuals = visuals;
		//this.worldView = new WorldView(this.visualModel.world, this.cozyPack, this.texturePack);
	}

	handleModelUpdate() {
		if(this.visualModel.focus === focus.main) {
			this.mainRef.current?.focus();
		}
		this.forceUpdate();
	}

	handleKeydown(e: React.KeyboardEvent) {
		if(e.key === "Enter" && e.target === this.mainRef.current && this.visualModel.chatOpen === false) {
			this.visualModel.setChatOpen(true);
		}
	}

	componentDidMount() {
		this.visualModel.addUpdateListener((type) => this.handleModelUpdate());
		this.mainRef.current?.focus();
	}

	render(): React.ReactNode {
		return (
			<div
				tabIndex={0}
				ref={this.mainRef}
				className="gameview"
				onKeyDown={e => this.handleKeydown(e)}
			>
				<ModelContext.Provider value={[this.logicModel, this.visualModel]}>
					<ConditionalWorldView logicModel={this.logicModel} visualModel={this.visualModel} visuals={this.visuals} />
					<Chat />
					<button onClick={() => this.logicModel.disconnect()} className="dc-button">Disconnect</button>
				</ModelContext.Provider>
			</div>
		)
	}

}
import LogicModel from "model/LogicModel";
import React, { createContext, createRef } from "react";
import VisualModel, {focus, Position, UpdateTypes} from "visual_model/VisualModel";
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
import EscapeMenu from "./ui/escapemenu/EscapeMenu";
import InventoryMenu from "./ui/inventory/InventoryMenu";

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
		this.logicModel = logicModel;
		this.visualModel = visualModel;
		this.visuals = visuals;
		//this.worldView = new WorldView(this.visualModel.world, this.cozyPack, this.texturePack);
	}

	handleModelUpdate(e: UpdateTypes) {
		if(this.visualModel.focus === "main") {
//			console.log("focused main");
			this.mainRef.current?.focus();
		}
		this.forceUpdate();
	}

	handleKeydown(e: React.KeyboardEvent) {
		if(e.key === "Escape" && this.visualModel.focus === "chat") {
			//chat lost input focus
			this.visualModel.setChatOpen(false);
			return;
		}
		if(e.key === "Escape" && this.visualModel.focus === "main") {
			this.visualModel.setMenuOpen(true);
			return;
		}
		if(e.key === "Enter") {
			this.visualModel.setChatOpen(true);
			return;
		}
		if(this.visualModel.focus === "main") {
			if(e.key === "e" || e.key === "E") {
				this.visualModel.setInventoryOpen(true);
			}
		}
	}

	componentDidMount() {
		this.visualModel.addUpdateListener((type) => this.handleModelUpdate(type));
		if(this.visualModel.focus === "main") {
			this.mainRef.current?.focus();
//			console.log("focused gameview");
		}
	}

	render(): React.ReactNode {
		let escapeMenu = this.visualModel.menuOpen ? <EscapeMenu /> : null;
		let inventoryMenu = this.visualModel.inventoryOpen ? <InventoryMenu texturePack={this.props.visuals.textures} model={this.visualModel} /> : null;

		let content = (
			<div
				tabIndex={0}
				ref={this.mainRef}
				className="gameview"
				onKeyDown={e => this.handleKeydown(e)}
			>
				<ModelContext.Provider value={[this.logicModel, this.visualModel]}>
					<ConditionalWorldView logicModel={this.logicModel} visualModel={this.visualModel} visuals={this.visuals} />
					<Chat />
					{escapeMenu}
					{inventoryMenu}
				</ModelContext.Provider>
			</div>
		)
		return content;
/* 		if(this.visualModel.dead) {
			return <DeadLayer>{content}</DeadLayer>
		}
		else {
			return content;
		}
 */	}

}
import LogicModel from "model/LogicModel";
import React, { createContext, createRef } from "react";
import VisualModel, {focus, Position} from "visual_model/VisualModel";
import GraphicsComponent from "./graphics/component/GraphicsComponent";
import WorldView from "./graphics/layers/WorldView";
import Chat from "./ui/chat/Chat";

import "./GameView.scss";

export type props = {
	logicModel: LogicModel
	visualModel: VisualModel
}

export type Models = [LogicModel, VisualModel];

export const ModelContext = createContext<Models>(null!);

const ignore = () => {};

export default class GameView extends React.Component<props, {}> {

	private worldView: WorldView
	private logicModel: LogicModel
	private visualModel: VisualModel
	private mainRef = createRef<HTMLDivElement>();

	constructor(props: props) {
		super(props);

		this.logicModel = props.logicModel;
		this.visualModel = props.visualModel;

		this.visualModel.setUpdateCallback(() => this.handleModelUpdate());

		this.worldView = new WorldView(this.visualModel);
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
		this.mainRef.current?.focus();
	}

	handleWheel(e: React.WheelEvent) {
		this.visualModel.multiplyZoom(1 - (e.deltaY / 1000));
	}

	handleMouseDown(e: React.MouseEvent) {
		if(e.target !== this.mainRef.current) {
			return;
		}
		let {offsetX, offsetY} = e.nativeEvent;
		let [logicX, logicY] = this.worldView.translateCanvasXY(offsetX, offsetY);

		this.logicModel.moveMeTo(logicX, logicY);
	}

	render(): React.ReactNode {
		return (
			<div onWheel={e => this.handleWheel(e)} onMouseDown={e => this.handleMouseDown(e)} tabIndex={0} ref={this.mainRef} className="gameview" onKeyDown={e => this.handleKeydown(e)}>
				<ModelContext.Provider value={[this.logicModel, this.visualModel]}>
					<GraphicsComponent model={this.visualModel} view={this.worldView} />
					<Chat />
					<button onClick={() => this.logicModel.disconnect()} className="dc-button">Disconnect</button>
				</ModelContext.Provider>
			</div>
		)
	}

}
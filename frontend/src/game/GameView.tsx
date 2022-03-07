import LogicModel from "model/LogicModel";
import React, { createContext, createRef } from "react";
import VisualModel, {focus, Position} from "visual_model/VisualModel";
import GraphicsComponent from "./graphics/component/GraphicsComponent";
import WorldView from "./graphics/worldview/WorldView";
import Chat from "./ui/chat/Chat";

import "./GameView.scss";
import CozyPack from "./graphics/texture/CozyPack";
import ImageStore from "./ImageStore";
import TexturePack from "./graphics/texture/TexturePack";

export type props = {
	logicModel: LogicModel
	visualModel: VisualModel,
	imageStore: ImageStore
}

export type Models = [LogicModel, VisualModel];

export const ModelContext = createContext<Models>(null!);

export default class GameView extends React.Component<props, {}> {

	private worldView: WorldView
	private logicModel: LogicModel
	private visualModel: VisualModel
	private mainRef = createRef<HTMLDivElement>();
	private intervalTask: number | undefined = undefined
	private cozyPack: CozyPack
	private texturePack: TexturePack

	private mousePositionX: number = 0;
	private mousePositionY: number = 0;

	constructor(props: props) {
		super(props);

		this.cozyPack = new CozyPack(this.props.imageStore);
		this.texturePack = new TexturePack();

		this.logicModel = props.logicModel;
		this.visualModel = props.visualModel;

		this.visualModel.addUpdateListener((type) => this.handleModelUpdate());
		this.worldView = new WorldView(this.visualModel, this.cozyPack, this.texturePack);
	}

	componentWillUnmount() {
		clearInterval(this.intervalTask);
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
		if(e.target instanceof Element) {
			if(e.target.matches(".nozoom") || e.target.matches(".nozoom *")) {
				return;
			}
		}
		this.visualModel.multiplyZoom(1 - (e.deltaY / 500));
	}

	handleMouseDown(e: React.MouseEvent) {
		if(e.target !== this.mainRef.current) {
			return;
		}

		const moveToOffset = (a: number, b: number) => {
			let [logicX, logicY] = this.worldView.translateCanvasXY(a, b);
			this.logicModel.moveMeTo(logicX, logicY);
		}

		moveToOffset(e.nativeEvent.offsetX, e.nativeEvent.offsetY);
		let h: TimerHandler = () => moveToOffset(this.mousePositionX, this.mousePositionY);
		this.intervalTask = setInterval(h, 250);
	}

	handleMouseUp(e: React.MouseEvent<HTMLDivElement, MouseEvent>) {
		clearInterval(this.intervalTask);
		this.intervalTask = undefined;
	}

	handleMouseMove(e: React.MouseEvent<HTMLDivElement, MouseEvent>) {
		this.mousePositionX = e.nativeEvent.offsetX
		this.mousePositionY = e.nativeEvent.offsetY;
	}

	render(): React.ReactNode {
		return (
			<div
				onWheel={e => this.handleWheel(e)}
				onMouseDown={e => this.handleMouseDown(e)}
				tabIndex={0}
				ref={this.mainRef}
				className="gameview"
				onKeyDown={e => this.handleKeydown(e)}
				onMouseUp={e => this.handleMouseUp(e)}
				onMouseMove={e => this.handleMouseMove(e)}
			>
				<ModelContext.Provider value={[this.logicModel, this.visualModel]}>
					<GraphicsComponent renderable={this.worldView} />
					<Chat />
					<button onClick={() => this.logicModel.disconnect()} className="dc-button">Disconnect</button>
				</ModelContext.Provider>
			</div>
		)
	}

}
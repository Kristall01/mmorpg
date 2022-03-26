import VisualResources from "game/VisualResources";
import LogicModel from "model/LogicModel";
import React, { Component, createRef } from "react";
import VisualModel, { UpdateTypes } from "visual_model/VisualModel";
import World from "visual_model/World";
import GraphicsComponent from "../component/GraphicsComponent";
import Renderable from "../Renderable";
import EmptyRenderer from "../renderers/empty/EmptyRenderer";
import WorldRenderer from "../renderers/world/WorldRenderer";
import CozyPack from "../texture/CozyPack";
import TexturePack from "../texture/TexturePack";
import ConditionalWorldView from "./ConditionalWorldView";
import "./WorldView.scss";

export type props = {
	logicModel: LogicModel
	visualModel: VisualModel,
	world: World,
	visuals: VisualResources
}

class WorldView extends React.Component<props> {

	private logicModel: LogicModel
	private visualModel: VisualModel

	private mousePositionX: number = 0;
	private mousePositionY: number = 0;
	private worldRenderer: WorldRenderer

	private intervalTask: number | undefined = undefined
	private mainRef = createRef<HTMLDivElement>();

	constructor(props: props) {
		super(props);
		this.visualModel = props.visualModel;
		this.logicModel = props.logicModel;
		this.worldRenderer = new WorldRenderer(props.world, props.visuals);

	}

	componentDidMount() {
		this.visualModel.addUpdateListener(l => {
			if(l === "maxfps") {
				this.forceUpdate();
			}
		})
	}

	shouldComponentUpdate(nextProps: props, nextState: {}) {
/* 		if(visualModel.focus === "chat") {
			focusChat();
		}
		if(visualModel.chatOpen === false) {
			setChatText("");
		}
		dummyDiv.current?.scrollIntoView();
		window.scrollTo(0,document.body.scrollHeight);
	}, [visualModel.focus, visualModel.chatlog, visualModel.chatOpen]);
 */
		if(this.visualModel.focus === "main") {
			this.mainRef.current?.focus();
			//console.log("focused worldview");
		}
		return false;
	}

	handleKeyDown(e: React.KeyboardEvent) {
		let lowercase = e.key.toLowerCase();
		//console.log("handled keydown");
		if(lowercase === "a") {
			//console.log("spell casted!");
			this.logicModel.collectNearbyItems();
			e.stopPropagation();
		}
	}

	handleMouseUp(e: React.MouseEvent<HTMLDivElement, MouseEvent>) {
		this.clearIntervalTask();
	}

	clearIntervalTask() {
		clearInterval(this.intervalTask);
		this.intervalTask = undefined;
	}

	handleMouseMove(e: React.MouseEvent<HTMLDivElement, MouseEvent>) {
		this.mousePositionX = e.nativeEvent.offsetX
		this.mousePositionY = e.nativeEvent.offsetY;
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
		this.clearIntervalTask();

		if(e.target !== this.mainRef.current) {
			return;
		}

		const moveToOffset = (a: number, b: number) => {
			let [logicX, logicY] = this.worldRenderer.translateCanvasXY(a, b);
			this.logicModel.moveMeTo(logicX, logicY);
		}

		moveToOffset(e.nativeEvent.offsetX, e.nativeEvent.offsetY);
		let h: TimerHandler = () => moveToOffset(this.mousePositionX, this.mousePositionY);
		this.intervalTask = setInterval(h, 250);
	}

	componentWillUnmount() {
		this.clearIntervalTask();
	}

	render() {
		return (
			<div tabIndex={-1}
				ref={this.mainRef}
				className="world-view"
				onWheel={e => this.handleWheel(e)}
				onMouseDown={e => this.handleMouseDown(e)}
				onMouseUp={e => this.handleMouseUp(e)}
				onMouseMove={e => this.handleMouseMove(e)}
				onKeyDown={e => this.handleKeyDown(e)}
			>
				<GraphicsComponent maxFPS={this.visualModel.maxFPS} renderable={this.worldRenderer} />
			</div>
		);
	}

};

export default WorldView;
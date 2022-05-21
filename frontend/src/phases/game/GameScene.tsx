import GameView from "game/GameView";
import MenuContext from "MenuContext";
import {IEventReciever, ModelEvent, ModelEventType} from "model/Definitions";
import LogicModel from "model/LogicModel";
import MenuScene from "phases/menu/MenuScene";
import { Component, Context, createContext, useState } from "react";
import VisualModel from "visual_model/VisualModel";

import "./GameScene.scss";
import VisualResources from "game/VisualResources";
import { Button } from "react-bootstrap";

type props = {
	modelGenerator: (callback: IEventReciever) => LogicModel
	visuals: VisualResources
}

type state = {
	text: string | null,
	model: VisualModel,
	ended: boolean
}

class GameScene extends Component<props, state>  {

	private model: LogicModel | null = null

	static contextType = MenuContext;

	constructor(props: props) {
		super(props);

		this.handleEvent = this.handleEvent.bind(this);

		this.state = {
			text: "Kapcsolódás...",
			model: new VisualModel(),
			ended: false
		}
	}

	handleEvent(event: ModelEvent) {
		switch(event.type) {
			case ModelEventType.CONNECTED: {
				this.change("Bejelentkezés...", false);
				break;
			}
			case ModelEventType.PLAY: {
				this.change(null, false);
				break;
			}
			case ModelEventType.END: {
				if(event.data) {
					this.change(event.data, true);
				}
				else {
					this.context(<MenuScene visuals={this.props.visuals} />);
				}
			}
		}
	}

	private change(text: string | null, ended: boolean) {
		let ob: any = {};
		ob.text = text;
		ob.ended = ended;
		this.setState(Object.assign({}, this.state, ob));
	}

	componentDidMount() {
		//THIS MUST STAY HERE, CANT GO TO CONSTRUCTOR !!!!
		this.model = this.props.modelGenerator({handleEvent: this.handleEvent, handleSignal: this.state.model.handleSignal});
	}

	render() {
		let buttonElement = this.state.ended ? (
			<Button className="button" variant="primary" onClick={() => this.context(<MenuScene visuals={this.props.visuals} />)}>
				<div className="gapper">
					<i className="fa-solid fa-left"></i>
					<span>Vissza a menübe</span>
				</div>
			</Button>
		) : null
		if(this.state.text) {
			return <div className="game-phase-component">
				<div className="text">
					{this.state.text}
				</div>
				{buttonElement}
			</div>
		}
		return <GameView visuals={this.props.visuals} logicModel={this.model!} visualModel={this.state.model} />
	}

}

export default GameScene;
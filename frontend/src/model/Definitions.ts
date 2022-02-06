import VisualModel from "visual_model/VisualModel";
//import SignalOut from "./signals/SignalOut";

export enum ModelEventType {
	CONNECTED,
	PLAY,
	END,
}

export interface ModelEvent {
	type: ModelEventType
	data?: any
}

export type EventHandler = (event: ModelEvent) => void;
export type SignalHandler = (signal: SignalIn) => void;

export interface IEventReciever {
	handleEvent: EventHandler;
	handleSignal: SignalHandler;
}

export interface SignalIn {
	execute: (model: VisualModel) => void
}

/* export interface IModel {

	sendSignal: (signal: SignalOut) => void
	disconnect: () => void

} */

/* export abstract class SimpleModel implements IModel {

	protected eventReciever: IEventReciever

	constructor(callback: IEventReciever) {
		this.eventReciever = callback;
	}

	abstract sendSignal(signal: SignalOut): void;

	abstract disconnect(): void;

	protected broadcastSignal(signal: SignalIn) {
		this.eventReciever.handleSignal(signal);
	}

	protected broadcastEvent(eventType: ModelEvent) {
		this.eventReciever.handleEvent(eventType);
	}

} */
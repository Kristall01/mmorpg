import { ColoredCloth } from "game/graphics/renderers/world/HumanRenderer";
import { IEventReciever, ModelEvent, SignalIn } from "./Definitions";
//import SignalOutChat from "./signals/out/SignalOutChat";
//import SignalOut from "./signals/SignalOut";

export default abstract class LogicModel {

	protected eventReciever: IEventReciever

	constructor(callback: IEventReciever) {
		this.eventReciever = callback;
	}

	//abstract sendSignal(signal: SignalOut): void;

	abstract disconnect(): void;

	protected broadcastSignal(signal: SignalIn) {
		this.eventReciever.handleSignal(signal);
	}

	protected broadcastEvent(eventType: ModelEvent) {
		this.eventReciever.handleEvent(eventType);
	}

	abstract moveMeTo(x: number, y: number): void;

	abstract sendChatMessage(message: string): void;

	abstract collectNearbyItems(): void;

	abstract applyClothes(clothes: ColoredCloth[]): void;

	abstract attackTowards(x: number, y: number): void;

}
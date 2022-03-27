import React from "react";
import SubManager from "SubManager";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";

export default abstract class ConnectedComponent<T = any, props = {},S = {}, SS = {}> extends React.Component<props, S, SS> {

	private subManager: SubManager = new SubManager();
	private models: Array<UpdateBroadcaster<T>>

	constructor(props: props, models: Array<UpdateBroadcaster<T>>) {
		super(props);
		this.models = models;
	}

	componentDidMount() {
		for(let m of this.models) {
			this.subManager.subscribe(m, (t) => this.handleEvent(t));
		}
	}

	protected handleEvent(event: T) {
		this.forceUpdate();		
	}

	componentWillUnmount() {
		this.subManager.removeAll();
	}

	abstract render(): React.ReactNode;

}
import React from "react";
import SubManager from "SubManager";
import UpdateBroadcaster from "visual_model/UpdateBroadcaster";

export default abstract class ConnectedComponent<props = {},S = {}, SS = {}> extends React.Component<props, S, SS> {

	private subManager: SubManager = new SubManager();
	private models: Array<UpdateBroadcaster<any>>

	constructor(props: props, models: Array<UpdateBroadcaster<any>>) {
		super(props);
		this.models = models;
	}

	componentDidMount() {
		let updater = () => this.forceUpdate();
		for(let m of this.models) {
			this.subManager.subscribe(m, updater);
		}
	}

	componentWillUnmount() {
		this.subManager.removeAll();
	}

	abstract render(): React.ReactNode;

}
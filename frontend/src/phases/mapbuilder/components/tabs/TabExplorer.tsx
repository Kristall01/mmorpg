import { useContext, useEffect, useState } from "react";
import { MapbuildModelContext } from "../../MapBuilder";
import TabModel from "../../model/TabModel";
import { Events } from "../../model/TabManager";
import TabLabel from "./TabLabel";

const TabExplorer = ({}) => {

	const model = useContext(MapbuildModelContext);

	let tabMan = model.getTabManager();

	let [tabLabels, setTabLabels] = useState<Array<TabModel>>([]);
	let [activeTab, setActiveTab] = useState<TabModel | null>(tabMan.getActiveTab())

	const handleUpdate = (type: Events) => {
		if(type === "added" || type === "removed") {
			let a: TabModel[] = []
			for(let tab of model.getTabManager().getTabs()) {
				a.push(tab);
			}
			setTabLabels(a);
			return;
		}
		if(type === "activate") {
			setActiveTab(tabMan.getActiveTab());
		}
	}

	useEffect(() => {
		let a = model.getTabManager().addUpdateListener(handleUpdate);
		return () => {
			model.getTabManager().removeUpdateListener(a);
		}
	}, []);

	return (
		<div className="tab-explorer">
			<div className="tablist">
				{tabLabels.map((a,b) => <TabLabel active={a.isActive()} activeHandler={a.select.bind(a)} key={b} name={a.name} closeHandler={a.close.bind(a)} />)}
			</div>
			<div className="tab-content">
				{tabLabels.map((a,b) => {
					if(a.isActive()) {
						return <div key={a.id}>{a.component}</div>;
					}
					else {
						return <div key={a.id} className="hidden">{a.component}</div>
					}
				})}
			</div>
		</div>
	)

};

export default TabExplorer;
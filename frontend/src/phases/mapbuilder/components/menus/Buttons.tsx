import { MapbuildModelContext } from "phases/mapbuilder/MapBuilder";
import { useContext } from "react";

const Buttons = ({}) => {

	const model = useContext(MapbuildModelContext);

	return (
		<div className="buttonlist">
			<div className="button">EXPORT</div>
			<div className="button"></div>
			{/**
				<div className="button" onClick={() => model.getTabManager().addTab("asd", t => <div>foo</div>)}>add tab</div>
			*/}
		</div>
	)

};

export default Buttons;
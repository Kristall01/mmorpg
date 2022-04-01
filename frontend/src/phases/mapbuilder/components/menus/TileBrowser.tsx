import { useContext, useEffect } from "react";
import { MapbuildModelContext, VisualResourcesContext } from "../../MapBuilder";

const TileBrowser = ({}) => {

	let model = useContext(MapbuildModelContext)

	let tiles: Array<JSX.Element> = [];

	let visuals = model.getProject().getVisuals();
	let tilesRaw = visuals.textures.getTextures("tile");

	if(tilesRaw !== undefined) {
		for(let tile of tilesRaw) {
			let className = ["tileline"];
			if(model.getActiveTexture()?.id === tile.id) {
				className.push("active");
			}

			//preview image disabled
	//		let imgTag = tile.img ? <img src={tile.img} /> : null
			let imgTag = null;
			tiles.push(<div onClick={() => {
				model.setActiveTile(tile);
			}} key={tile.id} className={className.join(" ")}>
				<div className="text">
					{tile.id}
				</div>
				<div className="right">
					<img src={tile.getSnapshot().src} />
				</div>
			</div>);
		}
	}

	return (
		<div className="tile-browser-component">
			<div className="buttonlist">
				<div className="button">importálás</div>
				<div className="button" onClick={() => model.setActiveTile(null)}>unselect</div>
			</div>
			<hr />
			<div className="tilelist">
				{tiles}
			</div>
		</div>
	)

};

export default TileBrowser;
import { useContext } from "react";
import { VisualResourcesContext } from "./MapBuilder";
import TileGrid from "./model/TileGrid";

export type props = {
	grid: TileGrid
	cellSize: number
}

const TileGridGomponent = ({grid, cellSize}: props) => {

	let rows = [];
	let visuals = useContext(VisualResourcesContext);

	for(let i = 0; i < grid.getHeight(); ++i) {
		let cols = [];
		for(let j = 0; j < grid.getWidth(); ++j) {
			let src = grid.elementAt([j, i])?.img;
			let content = src ? <img src={visuals.images.get(src).src} /> : null
			cols.push(<div onClick={() => grid.setElementAt([j,i], grid.model.getActiveTile())} className="cell" style={{width: cellSize, height: cellSize}} key={j}>
				{content}
			</div>)
		}
		rows.push(<div className="row" style={{height: cellSize}} key={i}>{cols}</div>)
	}

	return (
		<div className="tilegrid-parent">
			<div className="tilegrid">
				{rows}
			</div>
		</div>
	)

};

export default TileGridGomponent;
import { useContext } from 'react';
import { MapbuildModelContext } from '../MapBuilder';
import TileGrid from '../model/TileGrid';

const GridEditor = ({}) => {

	let model = useContext(MapbuildModelContext);

	let grid0 = model.getGrid()

	if(grid0 === null) {
		return <div>no grid to edit :/</div>;
	}
	let grid: TileGrid = grid0;
	return (
		<div className="buttonlist">
			<div className="button" onClick={() => grid.expandTop(1)}>expand top</div>
			<div className="button" onClick={() => grid.expandBottom(1)}>expand Bottom</div>
			<div className="button" onClick={() => grid.expandLeft(1)}>expand left</div>
			<div className="button" onClick={() => grid.expandRight(1)}>expand right</div>
		</div>
	)

};

export default GridEditor;
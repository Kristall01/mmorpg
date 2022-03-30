import { useContext } from 'react';
import { MapbuildModelContext } from '../../MapBuilder';
import TextureGridModel from '../../model/TextureGridModel';

const GridEditor = ({}) => {

	let model = useContext(MapbuildModelContext);

//	let grid0 = model.getGrid()
let grid0 = null;

	if(grid0 === null) {
		return <div>no grid to edit :/</div>;
	}
	let grid: TextureGridModel = grid0;
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
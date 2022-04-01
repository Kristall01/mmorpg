import ConnectedComponent from "ConnectedComponent";
import { ReactNode } from "react";
import Level, { LevelEvents } from "visual_model/Level";
import TextureGridModel, { TextureGridModelEvent } from "../model/TextureGridModel";

export type TexturGridProps = {
	cellsize: number,
	level: Level
}

class TextureGrid extends ConnectedComponent<LevelEvents, TexturGridProps> {

	constructor(props: TexturGridProps) {
		super(props, [props.level])
	}

	render(): ReactNode {
		let rows = [];
		const cellsize = this.props.cellsize;
		const {width, height} = this.props.level;
		for(let i = 0; i < height; ++i) {
			let cols: Array<ReactNode> = [];
			for(let j = 0; j < width; ++j) {
	//			let src = /* grid.elementAt([j, i])?.img */"";
	/* 			let content = src ? <img src={visuals.images.get(src).src} /> : null
				cols.push(<div onClick={() => grid.setElementAt([j,i], grid.model.getActiveTexture())} className="cell" style={{width: cellSize, height: cellSize}} key={j}>
					{content}
				</div>)*/
				cols.push(<div className="cell" style={{width: this.props.cellsize, height: "100%"}} key={`${i},${j}`} />);
			}
			rows.push(<div className="row" style={{height: cellsize}} key={i}>{cols}</div>)
		}
		return (
			<div className="tilegrid-parent">
				<div className="tilegrid">
					{rows}
				</div>
			</div>
		)

	}


}

export default TextureGrid;
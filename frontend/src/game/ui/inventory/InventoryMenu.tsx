import ConnectedComponent from 'ConnectedComponent';
import GraphicsComponent from 'game/graphics/component/GraphicsComponent';
import TextureRenderer from 'game/graphics/renderers/world/TextureRenderer';
import EmptyTexture from 'game/graphics/texture/EmptyTexture';
import TexturePack from 'game/graphics/texture/TexturePack';
import React, {createRef } from 'react';
import VisualModel, {UpdateTypes} from 'visual_model/VisualModel';
import World, { WorldEvent } from 'visual_model/World';
import { convertToHtml } from '../chat/textconverter';
import './InventoryMenu.scss';

export type InventoryMenuProps = {
	texturePack: TexturePack
	model: VisualModel
	world: World
}

class InventoryMenu extends ConnectedComponent<UpdateTypes | WorldEvent, InventoryMenuProps> {

	private mainRef;

	constructor(props: InventoryMenuProps) {
		super(props, [props.model, props.world]);
		this.mainRef = createRef<HTMLDivElement>();
	}

	handleEvent(t: UpdateTypes | WorldEvent) {
		if(t === "chat-open" || t === "inventory-open" || t === "item" || t === "inventory-update") {
			this.forceUpdate();
		}
	}

	private handleKeyDown(e: React.KeyboardEvent) {
		e.stopPropagation();
		if(e.key === "e" || e.key === "E" || e.key === "Escape") {
			this.props.model.setInventoryOpen(false);
		}
	}

	componentDidMount() {
		super.componentDidMount();
		if(this.props.model.focus === "inventory") {
			this.mainRef.current?.focus();
		}
	}

	shouldComponentUpdate(nextProps: Readonly<InventoryMenuProps>, nextState: Readonly<{}>, nextContext: any): boolean {
		if(this.props.model.focus === "inventory") {
			this.mainRef.current?.focus();
		}
		return false;
	}

/* 
	useEffect(() => {
		if(visualModel.focus === "inventory") {
			mainRef.current?.focus();
		}
	}, [visualModel.focus, visualModel.]);

	let item = texturePack.getTexture("APPLE", "item");

	let renderer: ReactNode = null;
	if(item !== undefined) {
		renderer = <GraphicsComponent maxFPS={1} renderable={new TextureRenderer(item)}  />
	} */

	render() {
		let items = [];
		let i = 0;
		for(let item of this.props.world.getItems()) {
			let itemName = item.item.name
			let titleElement = <span className="title" dangerouslySetInnerHTML={{__html: convertToHtml(itemName ?? item.item.type).innerHTML}} />
			items.push((
			<div key={i++} className='cell'>
				<GraphicsComponent
					renderable={new TextureRenderer(this.props.texturePack.getTexture(item.item.type, "item") || new EmptyTexture())}
					maxFPS={1}
					showFpsCounter={false}
				/>
				<div className="amount">
					{item.amount}
				</div>
				{titleElement}
			</div>))
		}
		return (
			<div ref={this.mainRef} onKeyDown={(e) => this.handleKeyDown(e)} tabIndex={-1} className="inventory-menu">
				<div className="inventory-holder">
					<div className="scroll-area">
						{items}
						{/* new Array(100).fill(0).map((a,b) => <div key={b} className='cell' />) */}
					</div>
				</div>
			</div>
		)
	}

};

export default InventoryMenu;
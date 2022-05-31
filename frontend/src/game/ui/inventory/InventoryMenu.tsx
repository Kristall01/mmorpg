import ConnectedComponent from 'ConnectedComponent';
import GraphicsComponent from 'game/graphics/component/GraphicsComponent';
import TextureRenderer from 'game/graphics/renderers/world/TextureRenderer';
import EmptyTexture from 'game/graphics/texture/EmptyTexture';
import TexturePack from 'game/graphics/texture/TexturePack';
import React, {createRef, ReactNode } from 'react';
import VisualModel, {Position, UpdateTypes} from 'visual_model/VisualModel';
import World, { WorldEvent } from 'visual_model/World';
import { parseTextHtml } from '../chat/textparser';
import './InventoryMenu.scss';

export type InventoryMenuProps = {
	texturePack: TexturePack
	model: VisualModel
	world: World,
}

type InventoryMenuState = {
	hoverPosition: Position | null,
	hoverElement: React.ReactNode
}

class InventoryMenu extends ConnectedComponent<UpdateTypes | WorldEvent, InventoryMenuProps, InventoryMenuState> {

	private mainRef;
	private lastHoverMove: number = 0

	constructor(props: InventoryMenuProps) {
		super(props, [props.model, props.world]);
		this.mainRef = createRef<HTMLDivElement>();

		this.state = {
			hoverElement: null,
			hoverPosition: null
		}
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
		return nextState !== this.state;
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

	handleMouseEnter(e: React.MouseEvent<HTMLDivElement, MouseEvent>, data: ReactNode) {
		this.setState({
			hoverPosition: [e.nativeEvent.clientX, e.nativeEvent.clientY],
			hoverElement: data
		})
	}

	handleMouseMove(e: React.MouseEvent<HTMLDivElement, MouseEvent>) {
		let now = performance.now();
		if(now - this.lastHoverMove < 20) {
			return;
		}
		this.lastHoverMove = now;
		this.setState(Object.assign({}, this.state, {
			hoverPosition: [e.clientX, e.clientY]
		}))
	}

	handleMouseLeave(e: React.MouseEvent<HTMLDivElement, MouseEvent>) {
		this.setState(Object.assign({}, this.state, {
			hoverPosition: null,
			hoverElement: null
		}))
	}

	render() {
		let items = [];
		let i = 0;
		for(let item of this.props.world.getItems()) {
			let titleElements: Array<React.ReactNode> = item.item.description.map((fragments,key) => {
				return (
					<div key={key} className="title" dangerouslySetInnerHTML={{__html: parseTextHtml(fragments).innerHTML}} />
				)
			})
			items.push(
				<div
					onMouseMove={e => this.handleMouseMove(e)}
					onMouseEnter={e => this.handleMouseEnter(e, titleElements)}
					onMouseLeave={e => this.handleMouseLeave(e)}
					key={i++}
					className='cell'
					>
					<GraphicsComponent
						renderable={new TextureRenderer(this.props.texturePack.getTexture(item.item.material, "item") || new EmptyTexture())}
						maxFPS={1}
						showFpsCounter={false}
					/>
					<div className="amount">
						{item.amount}
					</div>
				</div>
			);
		}
		let tooltipElement: ReactNode = null
		let hoverPosition = this.state.hoverPosition;
		if(hoverPosition !== null) {
			tooltipElement = <div className='item-tooltip' style={{top: hoverPosition[1], left: hoverPosition[0]}}>
				{this.state.hoverElement}
			</div>;
		}
		return (
			<div ref={this.mainRef} onKeyDown={(e) => this.handleKeyDown(e)} tabIndex={-1} className="inventory-menu">
				<div className="inventory-holder">
					<div className="scroll-area">
						{items}
						{/* new Array(100).fill(0).map((a,b) => <div key={b} className='cell' />) */}
					</div>
				</div>
				{tooltipElement}
			</div>
		)
	}

};

export default InventoryMenu;
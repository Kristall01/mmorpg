import GraphicsComponent from 'game/graphics/component/GraphicsComponent';
import { renderHuman } from 'game/graphics/renderers/world/EntityRenderer';
import { ColoredCloth } from 'game/graphics/renderers/world/HumanRenderer';
import CozyPack, { CozyActivity } from 'game/graphics/texture/CozyPack';
import React, { Component } from 'react';
import { Button } from 'react-bootstrap';
import { enumValueOf } from 'utils';
import { Activity, Cloth, ClothColor } from 'visual_model/assetconfig/HumanAssetConfig';
import { Direction } from 'visual_model/Paths';
import { runInThisContext } from 'vm';
import './ClothEditor.scss';
import ClothRenderer from './ClothRenderer';
import OpeningSection from './OpeningSection';

export type ClothEditorProps = {
	cozyPack: CozyPack,
	baseClothes?: ColoredCloth[]
	onApply?: (clothes: ColoredCloth[]) => void,
	onClose?: () => void,
}

const clothMapping = {
	TOP: "felsők",
	BOTTOM: "alsók",
	SHOES: "cipők",
	ALL: "jelmezek"
}

interface ClothEditorState {
	play: boolean,
	facing: number,
	activity: Activity
}

class ClothEditor extends Component<ClothEditorProps, ClothEditorState> {

	private clothRenderer: ClothRenderer;

	constructor(props: ClothEditorProps) {
		super(props);

		this.clothRenderer = new ClothRenderer(props.cozyPack);
		this.clothRenderer.skin = 0;

		this.state = {
			play: false,
			facing: 0,
			activity: Activity.enum.map.WALK
		}
		let baseClothes = this.props.baseClothes;
		if(baseClothes !== undefined) {
			for(let cloth of baseClothes) {
				this.clothRenderer.clothColor = cloth.color;
				this.clothRenderer.setClothAt(cloth.cloth.position, cloth.cloth);
			}
		}
	}

	shouldComponentUpdate(nextProps: Readonly<ClothEditorProps>, nextState: Readonly<ClothEditorState>, nextContext: any): boolean {
		this.clothRenderer.setPlaying(nextState.play);
		return true;
	}

	togglePlay() {
		this.setState({play: !this.state.play});
	}

	rotate() {
		let values = Direction.enum.values;
		let newValue = (this.state.facing+1) % values.length;
		this.setState({...this.state, facing: newValue});
		this.clothRenderer.facing = values[newValue];
	}

	handleAnimChange(e: React.ChangeEvent<HTMLSelectElement>) {
		let ac = enumValueOf(Activity.enum.map, e.target.value);
		if(ac === null) {
			e.preventDefault();
			return;
		}
		this.clothRenderer.activity = this.props.cozyPack.getCozyActivity(ac);
	}

	handleColorChange(e: React.ChangeEvent<HTMLSelectElement>) {
		let ac = enumValueOf(ClothColor.enum.map, e.target.value);
		if(ac === null) {
			e.preventDefault();
			return;
		}
		this.clothRenderer.clothColor = ac;
	}

	exportClothes() {
		if(this.props.onApply === undefined) {
			return;
		}
		this.props.onApply(this.clothRenderer.exportClothes());
	}

	render() {
		let closeBtn: React.ReactNode;
		if(this.props.onClose !== undefined) {
			closeBtn = <div title='ablak bezárása' onClick={this.props.onClose} className='close'><i className="fa-solid fa-x" /></div>
		}
		return (
			<div className="cloth-editor-component">
				<div className="left section">
					<div className="main">
						{Object.entries(clothMapping).map(([s0,s1],outerKey) => {
							return (
								<OpeningSection key={outerKey} title={s1}>
									<div>
										{Cloth.enum.values.filter(f => f.position === s0).map((a,innerKey) => (
											<div title={a.id} className='line' onClick={() => this.clothRenderer.setClothAt((s0 as any), a)} key={innerKey}>
												<div className="text">
													{a.label}
												</div>
											</div>
										))}
										<div title='semmi' className='line' onClick={() => this.clothRenderer.setClothAt((s0 as any), null)}>
											<div className="text">semmi</div>
										</div>
									</div>
								</OpeningSection>
							)
						})}
					</div>
				</div>
				<div className="right section">
					<div className="graphics">
						{closeBtn}
						<GraphicsComponent maxFPS={30} showFpsCounter={false} renderable={this.clothRenderer} />
					</div>
					<div className="control">
						<Button size='sm' onClick={() => this.togglePlay()}><i className={`fa-solid fa-${this.state.play ? "pause":"play"}`} /></Button>
						<Button size='sm' onClick={() => this.rotate()}>forgatás</Button>
						{this.props.onApply ? <Button size='sm' onClick={() => this.exportClothes()}>alkalmaz</Button>:null}
						<select defaultValue={this.state.activity.id.toUpperCase()} onChange={(e) => this.handleAnimChange(e)}>
							{Object.entries(Activity.enum.map).map(([name, activity],b) => <option value={name} key={b}>{activity.label}</option>)}
						</select>
						<select defaultValue="BLACK" onChange={(e) => this.handleColorChange(e)}>
							{Object.entries(ClothColor.enum.map).map(([name, color],b) => <option value={name} key={b}>{color.label}</option>)}
						</select>
					</div>
				</div>
			</div>
		)
	}

};

export default ClothEditor;
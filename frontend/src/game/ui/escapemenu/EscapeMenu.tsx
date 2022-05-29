import { ModelContext } from 'game/GameView';
import React, { createRef, useContext, useEffect, useState } from 'react';
import { WrappedButton } from 'shared/buttonmenu/ButtonMenu';
import BrickButton from './BrickButton';
import './EscapeMenu.scss';

const EscapeMenu = ({}) => {

	let [logicModel, visualModel] = useContext(ModelContext);
	let [fps, setFps] = useState(0);
	let mainRef = createRef<HTMLDivElement>();

	const handleFpsInput = (e: React.FormEvent<HTMLInputElement>) => {
		let num = parseInt(e.currentTarget.value);
		if(isNaN(num)) {
			return;
		}
		visualModel.maxFPS = num;
	}

	const handleVolumeInput = (e: React.FormEvent<HTMLInputElement>) => {
		let num = parseInt(e.currentTarget.value);
		if(isNaN(num)) {
			return;
		}
		visualModel.volume = num/100;
	}

	const onKeyDown = (e: React.KeyboardEvent) => {
		e.stopPropagation();
		if(e.key === "Escape") {
			visualModel.setMenuOpen(false);
		}
	}

	useEffect(() => {
		let fps = visualModel.maxFPS;
		if(fps === null) {
			fps = 0;
		}
		setFps(fps);

		if(visualModel.focus === "menu") {
			mainRef.current?.focus();
			//console.log("focused menu");
		}
	}, [visualModel.maxFPS, visualModel.focus, visualModel.volume]);
	let fpsText = fps === 0 ? "automatikus" : fps;
	return (
		<div ref={mainRef} onContextMenu={e => e.preventDefault()} tabIndex={-1} onKeyDown={onKeyDown} className="escape-menu-component">
			<div className="menu">
				<div className="row">
					<WrappedButton text='Kapcsolat bontása' onClick={() => {logicModel.disconnect()}}/>
				</div>
				<div className="row" />
				<div className="row slider-box">
					<div>max FPS: {fpsText}</div>
					<input value={fps} onInput={handleFpsInput} type="range" step={5} min={0} max={300} />
				</div>
				<div className="row slider-box">
					<div>hangerő: {Math.round(visualModel.volume*100)+"%"}</div>
					<input value={visualModel.volume*100} onInput={handleVolumeInput} type="range" step={1} min={0} max={100} />
				</div>
			</div>
		</div>
	)

};

export default EscapeMenu;
import { ModelContext } from 'game/GameView';
import React, { createRef, useContext, useEffect, useState } from 'react';
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
	}, [visualModel.maxFPS, visualModel.focus]);
	let fpsText = fps === 0 ? "automatikus" : fps;
	return (
		<div ref={mainRef} tabIndex={-1} onKeyDown={onKeyDown} className="escape-menu-component">
			<div className="menu">
				<div className="row">
					<BrickButton text='Kapcsolat bontása' onClick={() => {logicModel.disconnect()}}/>
				</div>
				<div className="row" />
				<div className="row fpseditor">
					<div>max FPS: {fpsText}</div>
					<input value={fps} onInput={handleFpsInput} type="range" step={5} min={0} max={300} />
				</div>
			</div>
		</div>
	)

};

export default EscapeMenu;
import buildno from 'buildno';
import { Component } from 'react';
import Menuswitch from '../Menuswitch';
import "./GameApp.scss";

class GameApp extends Component {

	render() {
		return (
			<div className="gameapp-component">
				<Menuswitch ></Menuswitch>
				<div className='buildno'>{buildno}</div>
			</div>
		)
	}
}

export default GameApp;

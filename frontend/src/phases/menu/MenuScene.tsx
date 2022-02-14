import { useContext } from "react"

import MenuContext from "MenuContext";
import GameScene from "phases/game/GameScene";
import DModel from "model/impl/demo/DModel";
import NetworkModel from "model/impl/ws/NetworkModel";

const MenuScene = () => {

	let setMenu = useContext(MenuContext);

	const startDemo = () => {
		let name = prompt("Add meg a neved");
		if(name === null) {
			return;
		}
		if(name.length < 3) {
			alert("A névnek legalább 3 karakternek kell lennie.");
			return;
		}
		setMenu(() => <GameScene modelGenerator={(a) => new DModel(a, name!)} />)
	}

	const startLocalhostModel = () => {
		let name = prompt("Add meg a neved");
		if(name === null) {
			return;
		}
		setMenu(() => <GameScene modelGenerator={(a) => new NetworkModel(a, "wss://rpg.ddominik.dev/ws", name!)} />)
	}

	return (
		<div>
			Válassz modellt!
			<button onClick={startDemo}>próba mód</button>
			<button onClick={startLocalhostModel}>online mód</button>
		</div>
	)
}

export default MenuScene;

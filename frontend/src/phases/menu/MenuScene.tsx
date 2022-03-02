import { useContext } from "react"

import MenuContext from "MenuContext";
import GameScene from "phases/game/GameScene";
import DModel from "model/impl/demo/DModel";
import NetworkModel from "model/impl/ws/NetworkModel";
import CozyPack from "game/graphics/texture/CozyPack";

interface props {
	cozyPack: CozyPack
}

const MenuScene = (props: props) => {

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
		setMenu(() => <GameScene cozyPack={props.cozyPack} modelGenerator={(a) => new DModel(a, name!)} />)
	}

	const startLocalhostModel = () => {
		let name = prompt("Add meg a neved");
		if(name === null) {
			return;
		}
		let address = prompt("szerver címe: ", "wss://rpg.ddominik.dev/ws");
		if(address === null) {
			return;
		}
		if(address.length === 0) {
			address = "ws://localhost:8080/ws";
		}
		setMenu(() => <GameScene cozyPack={props.cozyPack} modelGenerator={(a) => new NetworkModel(a, address!, name!)} />)
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

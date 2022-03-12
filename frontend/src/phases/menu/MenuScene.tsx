import { useContext } from "react"

import MenuContext from "MenuContext";
import GameScene from "phases/game/GameScene";
import DModel from "model/impl/demo/DModel";
import NetworkModel from "model/impl/ws/NetworkModel";
import CozyPack from "game/graphics/texture/CozyPack";
import ImageStore from "game/ImageStore";
import VisualResources from "game/VisualResources";
import LogicModel from "model/LogicModel";
import { IEventReciever } from "model/Definitions";

interface props {
	visuals: VisualResources
}

const MenuScene = ({visuals}: props) => {

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
		start((a) => new DModel(a, name!));
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
		start((a) => new NetworkModel(a, address!, name!));
	}

	const start = (generator: (callback: IEventReciever) => LogicModel) =>  {
		setMenu(() => <GameScene disconnectHandler={() => setMenu(<MenuScene visuals={visuals} />)} visuals={visuals} modelGenerator={generator} />)
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

import { useContext } from "react"

import MenuContext from "MenuContext";
import GameScene from "phases/game/GameScene";
import DModel from "model/impl/demo/DModel";
import NetworkModel from "model/impl/ws/NetworkModel";
import CozyPack from "game/graphics/texture/CozyPack";
import ImageStore from "game/ImageStore";
import VisualResources from "game/VisualResources";
import ButtonMenu, { WrappedButton } from "shared/buttonmenu/ButtonMenu";
import { Button } from "react-bootstrap";
import { LandingPhase } from "phases/landing/LandingPhase";
import ClothEditor from "game/ui/clotheditor/ClothEditor";

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
		setMenu(() => <GameScene visuals={visuals} modelGenerator={(a) => new DModel(a, name!)} />)
	}

	const getRecommendedAddress = (): string | undefined => {
		let {port, protocol, hostname} = window.location;
		switch(protocol) {
			case "http:": {
				protocol = "ws://"
				break;
			}
			case "https:": {
				protocol = "wss://";
				break;
			}
			default: {
				return undefined;
			}
		}
		if(port === "3000") {
			port = "8080";
		}
		return protocol+hostname+":"+port+"/ws";
	}

	const startNetworkModel = () => {
		let name = prompt("Add meg a neved");
		if(name === null) {
			return;
		}
		let address = prompt("szerver címe: ", getRecommendedAddress());
		if(address === null) {
			return;
		}
		setMenu(() => <GameScene visuals={visuals} modelGenerator={(a) => new NetworkModel(a, address!, name!)} />)
	}

	const menuBack = () => {
		setMenu(() => <LandingPhase />);
	}

	const startClothEditor = () => {
		setMenu(() => <div style={{height: "100%", display: "flex", justifyContent: "center", alignItems: "center"}}><ClothEditor cozyPack={visuals.cozy} /></div>);
	}

	return (
		<ButtonMenu>
			<span>Válassz játékmódot!</span>
			<div className="btngroup">
				<WrappedButton text="teszt mód" onClick={startDemo} icon="fa-solid fa-flask" />
				<WrappedButton text="online mód" onClick={startNetworkModel} icon="fa-solid fa-earth-americas" />
				<WrappedButton text="ruha szerkesztő" onClick={startClothEditor} icon="fa-solid fa-clothes-hanger" />
			</div>
			<WrappedButton text="Vissza a főmenübe" onClick={menuBack} icon="fa-solid fa-left" />
		</ButtonMenu>
	)
}

export default MenuScene;

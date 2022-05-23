import { useContext } from "react"

import MenuContext from "MenuContext";
import GamePhase from "phases/game/GamePhase";
import DModel from "model/impl/demo/DModel";
import NetworkModel from "model/impl/ws/NetworkModel";
import VisualResources from "game/VisualResources";
import ButtonMenu, { WrappedButton } from "shared/buttonmenu/ButtonMenu";
import { LandingPhase } from "phases/landing/LandingPhase";
import ClothEditor from "game/ui/clotheditor/ClothEditor";
import InputScreen, { InputMap } from "shared/inputscreen/InputScreen";

interface props {
	visuals: VisualResources
}

const MenuPhase = ({visuals}: props) => {

	let setMenu = useContext(MenuContext);

	const startDemo = () => {
		let m: InputMap = new Map<string, string>();
		m.set("username", "felhasználónév");
		setMenu(() => <InputScreen buttonProps={{text: "kapcsolódás", icon: "fa-solid fa-arrow-right-to-bracket"}} submitHandler={(result) => {
			setMenu(() => <GamePhase visuals={visuals} modelGenerator={(a) => new DModel(a, result.get("username")!)} />)
		}} title="teszt mód" map={m} returnMenu={() => <MenuPhase visuals={visuals} />} />);
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
		let m: InputMap = new Map<string, string>();
		m.set("username", "felhasználónév");
		m.set("address", {defaultValue: getRecommendedAddress(), label: "szerver címe"});

		setMenu(() => <InputScreen buttonProps={{text: "kapcsolódás", icon: "fa-solid fa-arrow-right-to-bracket"}} submitHandler={(result) => {
			setMenu(() => <GamePhase visuals={visuals} modelGenerator={(a) => new NetworkModel(a, result.get("address")!, result.get("username")!)} />)
		}} title="Online mód" map={m} returnMenu={() => <MenuPhase visuals={visuals} />} />);
	}

	const menuBack = () => {
		setMenu(() => <LandingPhase />);
	}

	const startClothEditor = () => {
		setMenu(() => <div style={{height: "100%", display: "flex", justifyContent: "center", alignItems: "center"}}><ClothEditor visuals={visuals} /></div>);
	}

	return (
		<ButtonMenu>
			<span>Válassz játékmódot!</span>
			<div className="btngroup">
				<WrappedButton text="teszt mód" onClick={startDemo} icon="fa-solid fa-flask" />
				<WrappedButton text="online mód" onClick={startNetworkModel} icon="fa-solid fa-earth-americas" />
				{/*<WrappedButton text="ruha szerkesztő" onClick={startClothEditor} icon="fa-solid fa-clothes-hanger" />*/}
			</div>
			<WrappedButton text="Vissza a főmenübe" onClick={menuBack} icon="fa-solid fa-left" />
		</ButtonMenu>
	)
}

export default MenuPhase;

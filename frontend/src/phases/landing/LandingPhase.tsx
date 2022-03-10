import MenuContext from "MenuContext";
import LoadingScene from "phases/loading/LoadingScene";
import MenuScene from "phases/menu/MenuScene";
import { useContext } from "react";
import "./LandingPhase.scss";

export const LandingPhase = () => {

	let setMenu = useContext(MenuContext);

	return (
		<div className="landing">
			<div className="center">
				<button onClick={() => alert("Hamarosan...")}>dokumentáció</button>
				<button onClick={() => setMenu(() => <LoadingScene nextScene={v => <MenuScene visuals={v}/>} />)}>játék</button>
				<a href="https://github.com/Kristall01/mmorpg"><button >git repo</button></a>
			</div>
		</div>
	)

} 
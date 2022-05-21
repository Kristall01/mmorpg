import MenuContext from "MenuContext";
import LoadingScene from "phases/loading/LoadingScene";
import MenuScene from "phases/menu/MenuScene";
import { useContext } from "react";
import ButtonMenu, { WrappedButton } from "shared/buttonmenu/ButtonMenu";
import "./LandingPhase.scss";

export const LandingPhase = () => {

	let setMenu = useContext(MenuContext);

	return (
		<div className="landing">
			<ButtonMenu>
				<div>
					<div className="bigtext">MMORPG</div>
					<div className="smalltext">készítette Dudás Dominik</div>
				</div>
				<div className="btngroup">
					<WrappedButton icon="fa-solid fa-file-lines" onClick={() => alert("Hamarosan...")} text="dokumentáció megnyitása" />
					<WrappedButton icon="fa-solid fa-play" text="játék indítása" onClick={() => setMenu(() => <LoadingScene nextScene={v => <MenuScene visuals={v}/>} />)} />
					<WrappedButton icon="fa-brands fa-github" text="git repo" href="https://github.com/Kristall01/mmorpg" />
				</div>
			</ButtonMenu>
		</div>
	)

} 
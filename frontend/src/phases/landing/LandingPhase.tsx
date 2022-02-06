import MenuContext from "MenuContext";
import LoadingScene from "phases/loading/LoadingScene";
import { useContext } from "react";
import "./LandingPhase.scss";

export const LandingPhase = () => {

	let setMenu = useContext(MenuContext);

	return (
		<div className="landing">
			<div className="center">
				<button onClick={() => alert("Hamarosan...")}>dokumentáció</button>
				<button onClick={() => setMenu(() => <LoadingScene />)}>játék</button>
				<button onClick={() => alert("Hamarosan...")}>git repo</button>
			</div>
		</div>
	)

} 
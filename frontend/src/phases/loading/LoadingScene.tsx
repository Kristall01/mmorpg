import { useContext, useEffect, useState } from "react";

import "./LoadingScene.scss";

import MenuContext from "MenuContext";
import MenuScene from "phases/menu/MenuScene";
import TexturePack from "game/graphics/texture/TexturePack";
import CozyPack from "game/graphics/texture/CozyPack";
import ImageStore from "game/ImageStore";
import VisualResources from "game/VisualResources";
import { Button } from "react-bootstrap";
import { LandingPhase } from "phases/landing/LandingPhase";

export type props = {
	nextScene: (v: VisualResources) => React.SetStateAction<JSX.Element>
}

const LoadingScene = ({nextScene}: props) => {

	let [text, setText] = useState("Textúrák betöltése...");
	let [mounted, setMounted] = useState(true);
	let setMenu = useContext(MenuContext);
	let [backButton, setBackButton] = useState<boolean>(false);

	useEffect(() => {
		(async () => {
			let visualResources: VisualResources;
			try {
				visualResources = await VisualResources.load();
			}
			catch(err) {
				console.error(err);
				if(mounted) {
					setText("Betöltési hiba.");
					setBackButton(true);
				}
				return;
			}
			if(mounted) {
				setMenu(nextScene(visualResources));
			}
		})();
		return () => {
			setMounted(false);
		}
	})

	let bb = backButton ? <Button onClick={() => setMenu(() => <LandingPhase />)} variant="primary">vissza a főképernyőre</Button> : null

	return (
		<div className="loading-scene">
			{text}
			{bb}
		</div>
	);
}

export default LoadingScene;
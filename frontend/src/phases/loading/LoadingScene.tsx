import { useContext, useEffect, useState } from "react";

import MenuContext from "MenuContext";
import MenuScene from "phases/menu/MenuScene";
import TexturePack from "game/graphics/texture/TexturePack";
import CozyPack from "game/graphics/texture/CozyPack";
import ImageStore from "game/ImageStore";
import VisualResources from "game/VisualResources";

const LoadingScene = () => {

	let [text, setText] = useState("Betöltés...");
	let [mounted, setMounted] = useState(true);
	let setMenu = useContext(MenuContext);

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
				}
				return;
			}
			if(mounted) {
				setMenu(() => <MenuScene visuals={visualResources}/>);
			}
		})();
		return () => {
			setMounted(false);
		}
	})

	return <div>{text}</div>;
}

export default LoadingScene;
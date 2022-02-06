import { useContext, useEffect, useState } from "react";

import MenuContext from "MenuContext";
import MenuScene from "phases/menu/MenuScene";
import TexturePack from "game/graphics/texture/TexturePack";

const LoadingScene = () => {

	let [text, setText] = useState("Betöltés...");
	let [mounted, setMounted] = useState(true);
	let setMenu = useContext(MenuContext);

	useEffect(() => {
		(async () => {
			try {
				await TexturePack.loadAllTextures("/textures/texturepack.json");
			}
			catch(err) {
				if(mounted) {
					setText("Betöltési hiba.");
				}
				return;
			}
			if(mounted) {
				setMenu(() => <MenuScene />);
			}
		})();
		return () => {
			setMounted(false);
		}
	})

	return <div>{text}</div>;
}

export default LoadingScene;
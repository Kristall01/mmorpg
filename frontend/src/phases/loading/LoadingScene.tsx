import { useContext, useEffect, useState } from "react";

import MenuContext from "MenuContext";
import MenuScene from "phases/menu/MenuScene";
import TexturePack from "game/graphics/texture/TexturePack";
import CozyPack from "game/graphics/texture/CozyPack";

const LoadingScene = () => {

	let [text, setText] = useState("Betöltés...");
	let [mounted, setMounted] = useState(true);
	let setMenu = useContext(MenuContext);

	useEffect(() => {
		(async () => {
			let pack: CozyPack;
			try {
				await TexturePack.loadAllTextures("/textures/texturepack.json");
				pack = await CozyPack.createPack("/textures/cozy/");
			}
			catch(err) {
				console.error(err);
				if(mounted) {
					setText("Betöltési hiba.");
				}
				return;
			}
			if(mounted) {
				setMenu(() => <MenuScene cozyPack={pack}/>);
			}
		})();
		return () => {
			setMounted(false);
		}
	})

	return <div>{text}</div>;
}

export default LoadingScene;
import { useContext, useEffect, useState } from "react";

import MenuContext from "MenuContext";
import MenuScene from "phases/menu/MenuScene";
import TexturePack from "game/graphics/texture/TexturePack";
import CozyPack from "game/graphics/texture/CozyPack";
import ImageStore from "game/ImageStore";

const LoadingScene = () => {

	let [text, setText] = useState("Betöltés...");
	let [mounted, setMounted] = useState(true);
	let setMenu = useContext(MenuContext);

	useEffect(() => {
		(async () => {
			let imageStore: ImageStore;
			try {
				imageStore = new ImageStore();
				await imageStore.loadZip("/imagestore.zip");
				//let cozyPack = new CozyPack(imageStore);
				//let texturePack = new TexturePack(imageStore, "/textures/texturepack.json");
			}
			catch(err) {
				console.error(err);
				if(mounted) {
					setText("Betöltési hiba.");
				}
				return;
			}
			if(mounted) {
				setMenu(() => <MenuScene imageStore={imageStore}/>);
			}
		})();
		return () => {
			setMounted(false);
		}
	})

	return <div>{text}</div>;
}

export default LoadingScene;
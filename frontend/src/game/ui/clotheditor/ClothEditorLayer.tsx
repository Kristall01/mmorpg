import { ModelContext } from "game/GameView";
import { ColoredCloth } from "game/graphics/renderers/world/HumanRenderer";
import CozyPack from "game/graphics/texture/CozyPack";
import VisualResources from "game/VisualResources";
import { createRef, useContext } from "react";
import HumanEntity from "visual_model/entity/HumanEntity";
import EntityType from "visual_model/EntityType";
import ClothEditor from "./ClothEditor";

export type ClothEditorLayerProps = {
	visuals: VisualResources
}

const ClothEditorLayer = ({visuals}: ClothEditorLayerProps) => {

	const mainRef = createRef<HTMLDivElement>();

	const [logicModel, visualModel] = useContext(ModelContext);

	const closeWindow = () => {
		visualModel.setClotheditorOpen(false);
	}

	const handleKeyDown = (e: React.KeyboardEvent) => {
		e.stopPropagation();
		if(e.key === "r" || e.key === "R" || e.key === "Escape") {
			closeWindow();
		}
	}

	let clothes: ColoredCloth[] | undefined;

	let e = visualModel.world?.followedEntity;
	if(!(e == null || e == undefined || e.type !== EntityType.enum.map.HUMAN)) {
		clothes = [...(e as HumanEntity).clothes];
	}

	return (
		<div onContextMenu={e => e.preventDefault()} ref={mainRef} onKeyDown={handleKeyDown} tabIndex={-1} className="cloth-editor-layer">
			<ClothEditor onClose={closeWindow} baseClothes={clothes} onApply={c => logicModel.applyClothes(c)} visuals={visuals} />
		</div>
	)

};

export default ClothEditorLayer;
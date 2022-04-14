import { ModelContext } from "game/GameView";
import CozyPack from "game/graphics/texture/CozyPack";
import { createRef, useContext } from "react";
import { Cloth, ClothColor } from "visual_model/human/HumanAssetConfig";
import VisualModel from "visual_model/VisualModel";
import ClothEditor from "./ClothEditor";

export type ClothEditorLayerProps = {
	cozy: CozyPack
}

const ClothEditorLayer = ({cozy}: ClothEditorLayerProps) => {

	const mainRef = createRef<HTMLDivElement>();

	const [logicModel, visualModel] = useContext(ModelContext);

	const handleKeyDown = (e: React.KeyboardEvent) => {
		e.stopPropagation();
		if(e.key === "r" || e.key === "R" || e.key === "Escape") {
			visualModel.setClotheditorOpen(false);
		}
	}

	return (
		<div ref={mainRef} onKeyDown={handleKeyDown} tabIndex={-1} className="cloth-editor-layer">
			<ClothEditor baseClothes={[{color: ClothColor.enum.map.BLACK, cloth: Cloth.enum.map.BASIC}]} onApply={c => logicModel.applyClothes(c)} cozyPack={cozy} />
		</div>
	)

};

export default ClothEditorLayer;
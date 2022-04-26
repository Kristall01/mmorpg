import { ColoredCloth } from "game/graphics/renderers/world/HumanRenderer";
import { SignalIn } from "model/Definitions";
import { enumValueOf } from "utils";
import HumanEntity from "visual_model/entity/HumanEntity";
import { Cloth, ClothColor } from "visual_model/assetconfig/HumanAssetConfig";
import VisualModel from "visual_model/VisualModel";

export interface ColoredClothData {
	color: string,
	type: string
}

export default class SignalChangeClothes implements SignalIn {

	private clothes: ColoredClothData[];
	private entityID: number;

	constructor(entityID: number, clothes: ColoredClothData[]) {
		this.clothes = clothes;
		this.entityID = entityID;
	}

	execute(model: VisualModel): void {
		let e = model.world?.getEntity(this.entityID);


		let clothes: ColoredCloth[] = [];
		for(let clothData of this.clothes) {
			let cloth = enumValueOf(Cloth.enum.map, clothData.type);
			if(cloth === null) {
				console.warn(`Unknown cloth type '${clothData.type}'`)
				continue;
			}
			let color = enumValueOf(ClothColor.enum.map, clothData.color);
			if(color === null) {
				console.warn(`Unknown cloth color '${clothData.color}'`)
				continue;
			}
			clothes.push({cloth, color})
		}
		if(e !== undefined && e instanceof HumanEntity) {
			e.changeClothes(clothes);
		}
	}

}

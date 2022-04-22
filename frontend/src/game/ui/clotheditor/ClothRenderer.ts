import { StatelessRenderable } from "game/graphics/Renderable";
import { ColoredCloth, renderHuman } from "game/graphics/renderers/world/HumanRenderer";
import CozyPack, { CozyActivity } from "game/graphics/texture/CozyPack";
import { Activity, Cloth, ClothColor, ClothPosition, Skintone } from "visual_model/human/HumanAssetConfig";
import { Direction } from "visual_model/Paths";

export interface AnimationProperties {
	start: number,
	scale: number
}

export default class ClothRenderer extends StatelessRenderable {

	facing: Direction = Direction.enum.map.SOUTH;
	activity: CozyActivity
	private cozyPack: CozyPack
	skin: Skintone = 0
	private anim: AnimationProperties | null = null;
	clothes: Array<ColoredCloth | null> = [null,null,null,null]
	clothColor: ClothColor = ClothColor.enum.map.BLACK;

	constructor(cozyPack: CozyPack) {
		super();
		this.cozyPack = cozyPack;
		this.activity = cozyPack.getCozyActivity(Activity.enum.map.WALK);
	}

	exportClothes(): ColoredCloth[] {
		let allCloth = this.clothes[0];
		if(allCloth !== null) {
			return [allCloth];
		}
		let l: Array<ColoredCloth> = [];
		for(let i = 1; i < this.clothes.length; ++i) {
			let c = this.clothes[i];
			if(c !== null) {
				l.push(c);
			}
		}
		return l;
	}

	setClothAt(pos: ClothPosition, c: Cloth | null) {
		let cc = c === null ? null : {cloth: c, color: this.clothColor};
		if(pos === "ALL") {
			this.clothes[0] = cc;
			this.clothes[1] = null;
			this.clothes[2] = null;
			this.clothes[3] = null;
			return;
		}
		this.clothes[0] = null;
		switch(pos) {
			case "TOP": {
				this.clothes[1] = cc
				break;
			}
			case "BOTTOM": {
				this.clothes[2] = cc
				break;
			}
			case "SHOES": {
				this.clothes[3] = cc
				break;
			}
		}
	}

	setPlaying(play: boolean) {
		if(play) {
			this.anim = {
				scale: 1,
				start: performance.now()
			}
		}
		else {
			this.anim = null;
		}
	}

	render(renderTime: number, width: number, height: number): void {
		this.ctx.clearRect(0, 0, width, height);
		let minSize = Math.min(height, width);
		this.ctx.imageSmoothingEnabled = false;
		let animTime = this.anim === null ? 0 : (performance.now() - this.anim.start)*this.anim.scale;
		renderHuman(this.ctx, this.skin, this.facing, this.activity, animTime, [width/2,height/2], minSize, this.clothes);
	}
	
}
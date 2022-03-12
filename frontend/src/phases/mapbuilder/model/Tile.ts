export default class Tile {

	public readonly img: any
	public readonly name: string
	
	constructor(imgSrc: any, name: string) {
		this.img = imgSrc;
		this.name = name;
	}

}
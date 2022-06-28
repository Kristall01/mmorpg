interface jsondir {
	[key: string]: jsondir | null
}

export function loadImage(path: string): Promise<HTMLImageElement> {
	return new Promise((accept,reject) => {
		let a = new Image();
		a.src = path;
		if(a.complete) {
			accept(a);
			return;
		}
		a.addEventListener("load", () => {
			accept(a);
		});
		a.addEventListener("error", err => {
			reject(err);
		});
	});
}


export function loadAudio(path: string): Promise<HTMLAudioElement> {
	return new Promise((accept,reject) => {
		let a = new Audio(path);
		a.addEventListener("load", () => {
			accept(a);
		});
		a.addEventListener("error", err => {
			reject(err);
		});
	});
}

export interface ImageResource {
	img: HTMLImageElement;
	src: string;
}

const extensions: Map<string,string> = new Map();
extensions.set("mp3", "audio/mpeg");
extensions.set("wav", "audio/x-wav");

export default class ImageStore {

	private images: Map<string, ImageResource> = new Map();
	private audioBlobs: Map<string, string> = new Map();

	constructor() {
		(window as any).playAudio = this.playAudio.bind(this);
	}

	private loadImageDir(base: string, dir: jsondir): Promise<unknown> {
		let entries = Object.entries(dir);
		let proms: Array<Promise<unknown>> = new Array(entries.length);
		for(let i = 0; i < entries.length; ++i) {
			let [key, val] = entries[i];
			if(val === null) {
				let merged = base+key;
				proms[i] = loadImage(merged).then(i => this.images.set(key, {img: i, src: merged}));
			}
			else {
				proms[i] = this.loadImageDir(base+key+"/", val);
			}
		}
		return Promise.all(proms);
	}

	public async loadImage(packJsonPath: string, packBasePath: string) {
		let rawData = await fetch(packJsonPath);
		let data: jsondir = await rawData.json();
		await this.loadImageDir(packBasePath, data);
	}

	public async loadSound(packJsonPath: string, packBasePath: string) {
		let rawData = await fetch(packJsonPath);
		let data: jsondir = await rawData.json();
		//await this.loadImageDir(packBasePath, data);
	}

	public async loadImageBlob(blob: Blob, path: string): Promise<undefined> {
		let index = path.lastIndexOf('/');
		if(index !== -1) {
			path = path.substring(index+1);
		}
		let objectUrl = URL.createObjectURL(blob);
		const i = await loadImage(objectUrl);
		this.images.set(path, {img: i, src: objectUrl});
		return undefined;
	}

	public async loadSoundBlob(blob: Blob, path: string): Promise<undefined> {
		let index = path.lastIndexOf('/');
		if(index !== -1) {
			path = path.substring(index+1);
		}
		let lastIndex = path.lastIndexOf('.');
		if(lastIndex !== -1) {
			path = path.substring(0, lastIndex);
		}
		let objectUrl = URL.createObjectURL(blob);
		this.audioBlobs.set(path, objectUrl);
		return undefined;
	}

	public async loadSoundZip(path: string) {
		let zipjs: any = (window as any)["zip"];

		let zipBuffer = new Uint8Array(await (await fetch(path)).arrayBuffer());
		let zipReader = new zipjs.ZipReader(new zipjs.Uint8ArrayReader(zipBuffer));
		try {
			let entries = await zipReader.getEntries();
			let promises = [];

			for(let i = 0; i < entries.length; ++i) {
				if(entries[i].directory) {
					continue;
				}
				let nameBase: string = entries[i].filename;
				let extensionDot = nameBase.lastIndexOf('.');
				if(extensionDot === -1) {
					continue;
				}
				let fileExtension = nameBase.substring(extensionDot+1);

				let mimeType = extensions.get(fileExtension);
				if(mimeType !== undefined) {
					promises.push(this.loadSoundBlob(await entries[i].getData(new zipjs.BlobWriter(mimeType)), entries[i].filename));
				}
			}
			await Promise.all(promises);
		}
		catch(err) {}
		await zipReader.close();
	}

	public async loadImageZip(path: string) {
		let zipjs: any = (window as any)["zip"];

		let zipBuffer = new Uint8Array(await (await fetch(path)).arrayBuffer());
		let zipReader = new zipjs.ZipReader(new zipjs.Uint8ArrayReader(zipBuffer));
		try {
			let entries = await zipReader.getEntries();
			let promises = [];

			for(let i = 0; i < entries.length; ++i) {
				if(entries[i].directory) {
					continue;
				}
				promises.push(this.loadImageBlob(await entries[i].getData(new zipjs.BlobWriter("image/png")), entries[i].filename));
			}
			await Promise.all(promises);
		}
		catch(err) {}
		await zipReader.close();
	}
	

	/* public async loadSoundZip(path: string) {
		await this.loadZip(path, this.loadSoundBlob, "audio/mpeg")
	}

 	private async loadZip(path: string, method: (a: any, b: any) => void, format: string) {
		let zipjs: any = (window as any)["zip"];

		let zipBuffer = new Uint8Array(await (await fetch(path)).arrayBuffer());
		let zipReader = new zipjs.ZipReader(new zipjs.Uint8ArrayReader(zipBuffer));
		try {
			let entries = await zipReader.getEntries();
			let promises = [];

			for(let i = 0; i < entries.length; ++i) {
				if(entries[i].directory) {
					continue;
				}
				promises.push(method(await entries[i].getData(new zipjs.BlobWriter(format)), entries[i].filename));
			}
			await Promise.all(promises);
		}
		catch(err) {}
		await zipReader.close();
	} */

	getImage(key: string): ImageResource {
		let img = this.images.get(key);
		if(img === undefined) {
			throw new Error(`'${key}' image not found`);
		}
		return img;
	}

/* 	getAudio(key: string): string {
		let audio = this.audioBlobs.get(key);
		if(audio === undefined) {
			throw new Error(`'${key}' audio blob not found`);
		}
		return audio;
	} */

	playAudio(name: string, volume: number) {
		let a = new Audio(this.audioBlobs.get(name));
		a.volume = volume;
		a.play();
	}

}
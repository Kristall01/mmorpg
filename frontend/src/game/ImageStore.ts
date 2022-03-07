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

export interface ImageResource {
	img: HTMLImageElement;
	src: string;
}

export default class ImageStore {

	private images: Map<string, ImageResource> = new Map();

	private loadDir(base: string, dir: jsondir): Promise<unknown> {

		let a: HTMLImageElement = null!;

		let entries = Object.entries(dir);
		let proms: Array<Promise<unknown>> = new Array(entries.length);
		for(let i = 0; i < entries.length; ++i) {
			let [key, val] = entries[i];
			if(val === null) {
				let merged = base+key;
				proms[i] = loadImage(merged).then(i => this.images.set(key, {img: i, src: merged}));
			}
			else {
				proms[i] = this.loadDir(base+key+"/", val);
			}
		}
		return Promise.all(proms);
	}

	public async load(packJsonPath: string, packBasePath: string) {
		let rawData = await fetch(packJsonPath);
		let data: jsondir = await rawData.json();
		await this.loadDir(packBasePath, data);
	}

	public async loadBlob(blob: Blob, path: string): Promise<undefined> {
		let index = path.lastIndexOf('/');
		if(index !== -1) {
			path = path.substring(index+1);
		}
		let objectUrl = URL.createObjectURL(blob);
		const i = await loadImage(objectUrl);
		this.images.set(path, {img: i, src: objectUrl});
		return undefined;
	}

	public async loadZip(path: string) {
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
				promises.push(this.loadBlob(await entries[i].getData(new zipjs.BlobWriter("image/png")), entries[i].filename));
			}
			await Promise.all(promises);
			console.log(this.images);
		}
		catch(err) {}
		await zipReader.close();
	}

	get(key: string): ImageResource {
		let img = this.images.get(key);
		if(img === undefined) {
			throw new Error(`'${key}' image not found`);
		}
		return img;
	}

}
import Scene from "./Renderable";

const offscreenMaxFps = 10;

export class _fpsCounter {

	private oldfps: number = 0
	private fpsc: number = 0
	private time: number = 0

	private updateInterval = 1000;
	private fpsMultiplicator = 1000/this.updateInterval;

	tick(rendertime: number) {
		if(rendertime - this.time > this.updateInterval) {
			this.oldfps = this.fpsc * this.fpsMultiplicator;
			this.fpsc = 1;
			this.time = this.time + this.updateInterval;
			return this.oldfps;
		}
		else {
			this.fpsc++;
		}
	}

}

class RenderScheduler extends EventTarget {

	_baseElement: HTMLElement
	_canvasElement: HTMLCanvasElement
	_ctx: CanvasRenderingContext2D
	_width: number = 0
	_height: number = 0
	_maxFps: number | null = null
	_loopOperation: any
	_scene: Scene | null = null
	_canvasListeners: Map<string, Array<EventListener>>
	_resizeObserver: ResizeObserver | undefined
	_pauseDelayCounter: number
	_memoMaxFps: null | number
	_oldNow: number
	_timeStep: number = 0
	_fpsElement: HTMLDivElement

	_fpsCounter = new _fpsCounter()

	constructor(baseElement: HTMLElement) {
		super();
		//setup canvas element
			this._baseElement = baseElement;
			this._baseElement.innerHTML = "";
			this._canvasElement = document.createElement("canvas");
			this._baseElement.appendChild(this._canvasElement);
			this._canvasElement.tabIndex = 0;
			this._canvasElement.style.outline = "none";
			this._canvasElement.focus();
			this._ctx = this._canvasElement.getContext("2d", {alpha: false})!;

			this._fpsElement = document.createElement("div");
			this._fpsElement.classList.add("fpscounter");
			this._baseElement.appendChild(this._fpsElement);

			this._pauseDelayCounter = 0;
			//this._ctx.createImageData()

		//

		this._canvasListeners = new Map<string, Array<EventListener>>();

		this._subscribeToEvents();
		this.resize(baseElement.offsetWidth, baseElement.offsetHeight);

		this._loopLimited = this._loopLimited.bind(this);
		this._loopUnlimited = this._loopUnlimited.bind(this);
		this._runLoop = this._runLoop.bind(this);

		this.setMaxFps(null);
		this._memoMaxFps = null;
		this._oldNow = performance.now();
	}

	addListener(type: string, fn: EventListener) {
		let list = this._canvasListeners.get(type);
		if(list === undefined) {
			list = [];
			this._canvasListeners.set(type, list);
		}
		list.push(fn);
		this._canvasElement.addEventListener(type, fn);
	}

	clearListeners() {
		for(let key of Object.values(this._canvasListeners.keys())) {
			let l = this._canvasListeners.get(key);
			if(l) {
				for(let fn of l) {
					this._canvasElement.removeEventListener(key, fn);
				}
			}
		}
		this._canvasListeners.clear();
	}

	_subscribeToEvents() {
		this._resizeObserver = new ResizeObserver(() => {});
		this._resizeObserver.observe(this._baseElement);
		new ResizeObserver((e) => {
			let {width, height} = e[0].contentRect;
			this.resize(width, height);
		}).observe(this._baseElement);

		document.addEventListener("visibilitychange", () => {
			if (document.hidden){
				this._memoMaxFps = this._maxFps;
				if(this._maxFps == null) {
					this.setMaxFps(offscreenMaxFps);
				}
				else {
					this.setMaxFps(Math.min(offscreenMaxFps, this._maxFps));
				}
			} else {
				this.setMaxFps(this._memoMaxFps);
			}
		});
	}

	resize(width: number, height: number) {
		this._canvasElement.width = width;
		this._canvasElement.height = height;
		this._width = width;
		this._height = height;
	}

	setScene(scene: Scene | null) {
		this.clearListeners();
		let hadScene = this._scene;
		if(this._scene) {
			this._scene.unmount();
		}
		this._scene = scene;
		if(scene) {
			scene.mount(this._ctx);
			if(!hadScene) {
				this._setLoopOp();
				this._runLoop();
			}
		}
		else {
			this._loopOperation = () => {};
		}
	}

	_draw() {
		/*this._ctx.fillStyle = "#000";
		this._ctx.fillRect(0, 0, this._width, this._height);*/
		let now = performance.now();
		this._scene!.render(now, this._width, this._height);
		let tickResult = this._fpsCounter.tick(now);
		if(tickResult != undefined) {
			this._fpsElement.innerText = tickResult.toString()+" fps";
		}
	}

	_setLoopOp() {
		if(this._maxFps) {
			this._loopOperation = this._loopLimited;
		}
		else {
			this._loopOperation = this._loopUnlimited;
		}
	}

	setMaxFps(value: null | number) {
		this._maxFps = value;
		if(value) {
			this._timeStep = 1000 / value;
		}
		this._setLoopOp();
	}

	_runLoop() {
		this._loopOperation();
	}

	_sleepFor(time: number) {
		let end = performance.now() + time;
		while(end > performance.now()) {

		}
	}

 	_loopLimited() {
		this._draw();
		let then = performance.now();
		let renderTimeout = Math.max(0, this._timeStep - (then - this._oldNow));
		this._oldNow = performance.now() + renderTimeout;
		if(renderTimeout <= 0) {
			setTimeout(this._runLoop);
		}
		else {
			setTimeout(this._runLoop, renderTimeout)
		}
	}

	_loopUnlimited() {
		this._draw();
		requestAnimationFrame(this._runLoop);
	}

}

export default RenderScheduler;
import Renderable from "../Renderable";

const updateInterval = 250;

const fpsMultiplicator = 1000/updateInterval;

class fpscounter implements Renderable {

	private ctx: CanvasRenderingContext2D | null = null
	private oldfps: number = 0
	private fpsc: number = 0
	private time: number = 0

	render(rendertime: number, width: number, height: number) {
		if(rendertime - this.time > updateInterval) {
			this.oldfps = this.fpsc * fpsMultiplicator;
			this.fpsc = 1;
			this.time = this.time + updateInterval;
		}
		else {
			this.fpsc = this.fpsc + 1;
		}


		if(!this.ctx) {return};


		this.ctx.fillStyle = "#ffffff";
		this.ctx.fillRect(0, 0, 100, 40);
		this.ctx.fillStyle = "#000000";
		this.ctx.textBaseline = "top"
		this.ctx.font = " 20px Arial";
		this.ctx.fillText(this.oldfps+" fps", 10, 10);


		/*this.ctx!.beginPath();
		this.ctx!.arc(300, 300, 200, 0, Math.PI*2);
		this.ctx!.stroke();

		this.ctx.fillRect(Math.cos(rendertime/1000*Math.PI/5)*200 + 300-25, Math.sin(rendertime/1000*Math.PI/5)*200 + 300-25, 50, 50);*/
	}

	mount(ctx: CanvasRenderingContext2D) {
		this.ctx = ctx;

		this.time = performance.now();
		this.fpsc = 0;
		this.oldfps = 0;
	}

	unmount() {

	}
}

export default fpscounter;
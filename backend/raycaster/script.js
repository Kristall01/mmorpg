const root = document.querySelector("root")
const tablePlaceholder = document.getElementById("table-placeholder");

document.getElementById("read").addEventListener("click", () => {
	let val = window.prompt();
	if(val === null) {
		return;
	}
	generateTable(JSON.parse(val));
});

function addPoint(tableCells, index, points, bottom, left, resultClass) {
	if(index === points.length) {
		return;
	}
	let point = points[index];
	tableCells[point.y - bottom][point.x - left].classList.add(resultClass);
	setTimeout(() => {
		addPoint(tableCells, index+1, points, bottom, left, resultClass);
	});
}


function generateTable(data) {
	let width = data.right - data.left;
	let height = data.top - data.bottom;

	let canvas = document.createElement("canvas");

	const canvasHeight = height*50;
	canvas.width = width*50;
	canvas.height = canvasHeight;

	let left = data.left;
	let bottom = data.bottom;

	const ctx = canvas.getContext("2d");
	let fromPoint = data.from.map(v => v*50);
	let toPoint = data.to.map(v => v*50);
	ctx.moveTo(fromPoint[0] - left*50, canvasHeight - (fromPoint[1] - bottom*50));
	ctx.lineTo(toPoint[0] - left*50, canvasHeight - (toPoint[1] - bottom*50));
	ctx.stroke();

	let table = document.createElement("table");
	table.setAttribute("cellspacing",0);
	let tableCells = [];
	for(let i = 0; i < height; ++i) {
		let row = document.createElement("tr");
		let rowCells = [];
		for(let j = 0; j < width; ++j) {
			let col = document.createElement("td");
			row.appendChild(col);
			rowCells.push(col);
		}
		tableCells.push(rowCells);
		table.appendChild(row);
	}
	tableCells.reverse();

	document.getElementById("table-placeholder").innerHTML = "";
	tablePlaceholder.innerHTML = "";
	tablePlaceholder.appendChild(table);
	tablePlaceholder.appendChild(canvas);

	let result = data.result ? "good":"checked";

	setTimeout(() => {
		addPoint(tableCells, 0, data.sightpoints, bottom, left, result);
	})
}

fetch("/data.json").then(d => d.json()).then(generateTable)
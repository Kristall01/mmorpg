import MenuContext from "MenuContext";
import MapBuilder from "phases/mapbuilder/MapBuilder";
import MapbuildModel from "phases/mapbuilder/model/MapbuildModel";
import ProjectModel from "phases/mapbuilder/model/ProjectModel";
import React, { createRef, useContext } from "react";

const ProjectBrowser = ({}) => {

	let projectOpener = (window as any).showOpenFilePicker;


/* 	const handleClick = async () => {
		let a: FileSystemFileHandle;
		try {
			a = await projectOpener({multiple: false, types: [{description: "map editor project files", accept: {"application/octet-stream": ".mep"}}]});
		}
		catch(ex) {
			return;
		}

	}

	if(projectOpener === undefined) {
		return <div title="FileSystem api nem támogatott">Az Ön bönégszője elavult, ezért nem képes pálya szerkesztésre.</div>
	}

	return (
		<button onClick={handleClick}>projekt választás</button>
	) */

	const ref = createRef<HTMLInputElement>();
	const submitRef = createRef<HTMLInputElement>();

	let setMenu = useContext(MenuContext);

	const submit = (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		console.log("prevented submit");
		let data = Object.fromEntries(new FormData(e.currentTarget).entries())
		console.log(data);
	}

	const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		let files = e.target.files;
		if(files === null) {
			console.log("files are null")
			return;
		}
		if(files.length === 0) {
			return;
		}
		let f: File = files[0];
	}

	return (
		<>
			<button onClick={() => setMenu(() => <MapBuilder poject={ProjectModel.newProject()} />)}>új projekt készítése</button>
			{/** <button>projekt betöltése</button> */}
		</>
	)

/* 	<form onSubmit={submit}>
	<input onChange={handleChange} name="file" ref={ref} type="file"/>
	<input hidden type="submit" ref={submitRef}/>
	</form>
 */

};

export default ProjectBrowser;
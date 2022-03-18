export type props = {
	name: string,
	closeHandler: () => void
	activeHandler: () => void,
	active?: boolean
}

const TabLabel = ({name, closeHandler, activeHandler, active}: props) => {

	let classes = ["tab-label"];
	if(active) {
		classes.push("active");
	}

	return (
		<div className={classes.join(" ")} onClick={e => {
			if(e.target === e.currentTarget) {
				activeHandler();
			}
		}}>
			{name}
			<div className="close">
				<div className="cross" onClick={closeHandler}>
					<i className="fa-solid fa-xmark"></i>
				</div>
			</div>
		</div>
	)

};

export default TabLabel;
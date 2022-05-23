import MenuContext from "MenuContext";
import { LandingPhase } from "phases/landing/LandingPhase";
import { useState } from "react";

const defaultPhase = <LandingPhase />;

const Menuswitch = () =>  {

	let [fn, setMenu] = useState<React.ReactNode>(defaultPhase);
	return <MenuContext.Provider value={setMenu}>
		{fn}
	</MenuContext.Provider>;
}

export default Menuswitch;
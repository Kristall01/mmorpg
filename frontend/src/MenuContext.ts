import { createContext } from "react";

const MenuContext = createContext<React.Dispatch<React.SetStateAction<JSX.Element>>>(() => {});

export default MenuContext;
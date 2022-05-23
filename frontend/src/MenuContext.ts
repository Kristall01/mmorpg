import React, { createContext } from "react";

const MenuContext = createContext<React.Dispatch<React.SetStateAction<React.ReactNode>>>(() => {});

export default MenuContext;
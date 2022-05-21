import MenuContext from "MenuContext";
import React, { ReactNode, useContext } from "react";
import { createRef } from "react";
import { FormControl } from "react-bootstrap";
import ButtonMenu, { WrappedButton, WrappedButtonProps } from "shared/buttonmenu/ButtonMenu";
import "./InputScreen.scss";

export interface InputObject {
	label: string,
	defaultValue?: string,
}

export type InputMap = Map<string, InputObject|string>;

export type InputScreenProps = {
	map: InputMap
	title: string,
	returnMenu: () => React.ReactNode,
	submitHandler: (result: Map<string,string>) => void,
	buttonProps?: WrappedButtonProps
}

export const InputElement = ({label,name,defaultValue}: InputObject & {name: string}) => {
	return (
		<div className="input-element">
			<span>{label}</span>
			<FormControl defaultValue={defaultValue} type="text" name={name}></FormControl>
		</div>
	);
}

const InputScreen = ({map, title, returnMenu, buttonProps, submitHandler}: InputScreenProps) => {

	let inputs: Array<React.ReactNode> = [];

	let setMenu = useContext(MenuContext);

	for(let [key,val] of map.entries()) {
		let label: string;
		let defaultVal: string | undefined;
		if(typeof val === "string") {
			label = val;
		}
		else {
			label = val.label;
			defaultVal = val.defaultValue;
		}
		inputs.push(<InputElement label={label} name={key} defaultValue={defaultVal} key={key}></InputElement>);
	}

	let returnMenuElement: ReactNode;
	if(returnMenu) {
		returnMenuElement = <WrappedButton icon="fa-solid fa-left" text="vissza" onClick={() => setMenu(returnMenu())} />
	}

	let wrappedProps: WrappedButtonProps;
	if(buttonProps !== undefined) {
		wrappedProps = buttonProps;
	}
	else {
		wrappedProps = {
			text: "tovább"
		};
	}

	const formRef = createRef<HTMLFormElement>();

	const handleSubmit = () => {
		let form = formRef.current;
		if(form == null) {
			return;
		}
		let obj = new FormData(form).entries();
		let entries = Array.from(obj);
		let map = new Map<string,string>(entries.map(([val, value]) => ([val,value.toString()])));
		console.log(map);
		submitHandler(map);
	}

	return (
		<ButtonMenu>
			<form className="input-screen" ref={formRef} style={{display: "contents"}}>
				<h1>{title}</h1>
				<div className="btngroup">
					{inputs}
				</div>
				<div className="btnrow">
					{returnMenuElement}
					<WrappedButton submit onClick={handleSubmit} {...wrappedProps}></WrappedButton>
				</div>
			</form>
		</ButtonMenu>
	)
};

export default InputScreen;
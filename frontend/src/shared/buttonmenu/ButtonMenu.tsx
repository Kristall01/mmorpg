import React, { ReactNode } from 'react';
import { Button, ButtonProps } from 'react-bootstrap';
import './ButtonMenu.scss';

export type ButtonMenuProps = {
	children: ReactNode
}

export type WrappedButtonProps = {
	text: string,
	icon?: string,
	onClick?: (e: React.MouseEvent) => void,
	href?: string,
	submit?: boolean
}

export const WrappedButton = ({text, icon, onClick, href, submit}: WrappedButtonProps) => {
	
	let otherProps: ButtonProps = {};
	if(submit) {
		otherProps.type = "submit";
	}

	return (
		<div className="wrap">
			<Button className="button" variant="primary" href={href} onClick={onClick} {...otherProps}>
				<div className="gapper">
					{icon ? <i className={icon} /> : null}
					<span>{text}</span>
				</div>
			</Button>
		</div>
	)
}

const ButtonMenu = ({children}: ButtonMenuProps) => {

	return (
		<div className="button-menu">
			<div className="center">
				{children}
			</div>
		</div>
	)

};

export default ButtonMenu;
import React, { ReactNode } from 'react';
import { Button } from 'react-bootstrap';
import './ButtonMenu.scss';

export type ButtonMenuProps = {
	children: ReactNode
}

export type WrappedButtonProps = {
	text: string,
	icon?: string,
	onClick?: (e: React.MouseEvent) => void,
	href?: string
}

export const WrappedButton = ({text, icon, onClick, href}: WrappedButtonProps) => (
	<div className="wrap">
		<Button className="button" variant="primary" href={href} onClick={onClick}>
			<div className="gapper">
				{icon ? <i className={icon} /> : null}
				<span>{text}</span>
			</div>
		</Button>
	</div>

)

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
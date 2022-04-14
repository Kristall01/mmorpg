import { ReactNode, useState } from 'react';

export type OpeningSectionProps = {
	title: string,
	children?: ReactNode
}

const OpeningSection = ({title, children}: OpeningSectionProps) => {

	let [open, setOpen] = useState<boolean>(false);

	return (
		<div className={`opening-section${open?" open":""}`}>
			<div className="line" onClick={() => setOpen(!open)}>
				<i className={`arrow fa-solid fa-chevron-${open?"right":"down"}`}></i>
				<span className='text'>{title}</span>
			</div>
			<div className="content">
				{children}
			</div>
		</div>
	);

};

export default OpeningSection;
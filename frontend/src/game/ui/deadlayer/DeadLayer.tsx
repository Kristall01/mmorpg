import { Children } from 'react';
import './DeadLayer.scss';

type props = {
	children?: JSX.Element | JSX.Element[]
}

const DeadLayer = (props: props) => {

	console.log("rendering deadlayer");

	return (
		<div className='dead-layer-component'>
			{props.children}
			<div className="red"></div>
		</div>
	)

};

export default DeadLayer;
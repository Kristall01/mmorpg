import './BrickButton.scss';

export type Props = {
	text: string
	onClick: () => void
}

const BrickButton = ({onClick, text}: Props) => {

	return (
		<button className='brick' onClick={onClick} >{text}</button>
	)

};

export default BrickButton;
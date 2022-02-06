import { ModelContext } from "game/GameView";
import { createRef, FormEventHandler, forwardRef, KeyboardEventHandler, useContext, useEffect, useState } from "react"
import { focus } from "visual_model/VisualModel";

import "./textconverter.css";
import "./Chat.scss";

type props = {
	text: string
}

let inputRef = createRef<HTMLInputElement>();
let dummyDiv = createRef<HTMLDivElement>();

const Chat = (): JSX.Element | null => {

	let [logicModel, visualModel] = useContext(ModelContext);
	let [chatText, setChatText] = useState<string>("nul");

	/*useEffect(() => {
		if(model.chatContent !== null) {
			inputRef.current?.focus();
		}
		else {
			inputRef.current?.blur();
		}
	}, [model.chatlog, model.chatContent]);*/

	const handleType: FormEventHandler<HTMLInputElement> = (e) => {
		setChatText(e.currentTarget.value);
	}

	const handleKeyDown: KeyboardEventHandler<HTMLInputElement> = (e) => {
		if(e.key === "Escape") {
			setChatText("");
			visualModel.setChatOpen(false);
			e.preventDefault();
			return;
		}
		if(e.key === "Enter") {
			let chattext = e.currentTarget.value;
			if(chattext.trim().length !== 0) {
				let msg = e.currentTarget.value;
				if(msg === "/follow") {
					visualModel.followEntity(0);
				}
				else {
					logicModel.sendChatMessage(e.currentTarget.value);
				}
			}
			setChatText("");
			visualModel.setChatOpen(false);
			e.preventDefault();
		}
	}

	const focusChat = () => {
		inputRef.current?.focus();
	}

	useEffect(() => {
		if(visualModel.focus === focus.chat) {
			focusChat();
		}
		if(visualModel.chatOpen === false) {
			setChatText("");
		}
		dummyDiv.current?.scrollIntoView();
		window.scrollTo(0,document.body.scrollHeight);
	}, [visualModel.focus, visualModel.chatlog, visualModel.chatOpen])

/* 	{visualModel.chatlog.map((e,v) => <div key={v} dangerouslySetInnerHTML={{__html: e}}></div>)}
	<div ref={dummyDiv}></div>
 */

	return <div className={"chatwindow"+(visualModel.chatOpen ?" open":"")}>
		<div className="log">
			{visualModel.chatlog.map((e,v) => <div key={v} dangerouslySetInnerHTML={{__html: e}}></div>)}
			<div ref={dummyDiv}></div>
		</div>
		<div className="input-placeholder">
			<input ref={inputRef} onKeyDown={handleKeyDown} onChange={handleType} hidden={!visualModel.chatOpen} type="text" value={chatText} />
		</div>
	</div>

};

export default Chat;
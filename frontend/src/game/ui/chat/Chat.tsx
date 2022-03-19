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

	const handleLocalCommand = (cmd: string) => {
		if(cmd.length === 0) {
			visualModel.addChatEntry("§6§l[DEV] §r/dev parancsok:");
			visualModel.addChatEntry("§6§l[DEV] §7 - §r/dev camleak");
			visualModel.addChatEntry("§6§l[DEV] §7 - §r/dev maxzoom");
			visualModel.addChatEntry("§6§l[DEV] §7 - §r/dev maxfps");
			return;
		}
		cmd = cmd.substring(1);
		let split = cmd.split(" ");
		if(split[0] === "camleak") {
			visualModel.allowCamLeak = !visualModel.allowCamLeak;
			visualModel.addChatEntry("§6§l[DEV] §rCamleak átállítva");
			return;
		}
		if(split[0] === "maxzoom") {
			if(split.length === 1) {
				return;
			}
			try {
				visualModel.maxZoom = parseInt(split[1]);
				visualModel.addChatEntry("§6§l[DEV] §rMaxzoom átállítva");
			}
			catch(ex) {}
			return;
		}
		if(split[0] === 'maxfps') {
			let maxfps: number | null = null;
			if(split.length > 1) {
				try {
					maxfps = parseInt(split[1]);
				}
				catch(ex) {}
			}
			visualModel.maxFPS = maxfps;
			visualModel.addChatEntry("§6§l[DEV] §rMaxFPS átállítva");
			return;
		}
		visualModel.addChatEntry("§6§l[DEV] §rnincs ilyen parancs");
	}

	const handleKeyDown: KeyboardEventHandler<HTMLInputElement> = (e) => {
		if(visualModel.focus === "chat" && e.key === "Escape") {
			setChatText("");
			visualModel.setChatOpen(false);
			e.preventDefault();
			return;
		}
		if(e.key === "Enter") {
			let chattext = e.currentTarget.value;
			chattext = chattext.trim();
			if(chattext.length !== 0) {
				if(chatText.startsWith("/dev")) {
					handleLocalCommand(chatText.substring(4));
				}
				else {
					logicModel.sendChatMessage(chattext);
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
		if(visualModel.focus === "chat") {
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

	return <div className={"nozoom chatwindow"+(visualModel.chatOpen ?" open":"")}>
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
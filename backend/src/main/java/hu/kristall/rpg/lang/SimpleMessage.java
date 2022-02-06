package hu.kristall.rpg.lang;

import hu.kristall.rpg.lang.component.MessageComponent;

import java.util.Arrays;

public class SimpleMessage implements Message {
	
	private final Iterable<MessageComponent> components;
	private final String pattern;
	
	public SimpleMessage(String pattern, Iterable<MessageComponent> comp) {
		this.pattern = pattern;
		this.components = comp;
	}
	
	public SimpleMessage(String pattern, MessageComponent... components) {
		this.pattern = pattern;
		this.components = Arrays.asList(components);
	}
	
	@Override
	public String getPattern() {
		return pattern;
	}
	
	public String compute(Lang lang, String[] args) {
		StringBuilder base = new StringBuilder(64);
		int i = 0;
		for (MessageComponent component : components) {
			component.consume(base, lang, args);
			++i;
		}
		//System.out.println("SimpleMessage::apply() components length: "+i);
		return base.toString();
	}
	
}

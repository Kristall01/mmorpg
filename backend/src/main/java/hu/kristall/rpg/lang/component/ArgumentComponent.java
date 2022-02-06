package hu.kristall.rpg.lang.component;

import hu.kristall.rpg.lang.Lang;

public class ArgumentComponent implements MessageComponent {
	
	private int i;
	
	public ArgumentComponent(int arg) {
		this.i = arg;
	}
	
	@Override
	public void consume(StringBuilder base, Lang lang, String[] args) {
		base.append(i > args.length-1 ? '?' : args[i]);
	}
	
}

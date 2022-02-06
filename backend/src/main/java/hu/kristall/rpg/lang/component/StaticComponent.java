package hu.kristall.rpg.lang.component;


import hu.kristall.rpg.lang.Lang;

public class StaticComponent implements MessageComponent {
	
	private String content;
	
	public StaticComponent(String content) {
		this.content = content;
	}
	
	@Override
	public void consume(StringBuilder base, Lang lang, String[] args) {
		base.append(content);
	}
}

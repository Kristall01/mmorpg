package hu.kristall.rpg.lang.component;

import hu.kristall.rpg.lang.Lang;

public class QueryComponent implements MessageComponent {
	
	private String key;
	
	public QueryComponent(String key) {
		this.key = key;
	}
	
	@Override
	public void consume(StringBuilder base, Lang lang, String[] args) {
		base.append(lang.getMessage(key, args));
	}
	
}

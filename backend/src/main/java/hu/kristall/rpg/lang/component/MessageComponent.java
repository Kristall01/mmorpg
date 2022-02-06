package hu.kristall.rpg.lang.component;

import hu.kristall.rpg.lang.Lang;

public interface MessageComponent {

	 void consume(StringBuilder base, Lang lang, String[] args);
	
}

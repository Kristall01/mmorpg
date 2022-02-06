package hu.kristall.rpg.lang;

public interface Message {

	String compute(Lang lang, String[] params);
	default String getPattern() {return null;}

}

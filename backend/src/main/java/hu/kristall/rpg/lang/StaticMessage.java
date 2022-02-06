package hu.kristall.rpg.lang;

public class StaticMessage implements Message {
	
	private String message;
	private String pattern;
	
	public StaticMessage(String message, String pattern) {
		this.message = message;
		this.pattern = pattern;
	}
	
	@Override
	public String getPattern() {
		return pattern;
	}
	
	@Override
	public String compute(Lang lang, String[] strings) {
		return message;
	}
	
}

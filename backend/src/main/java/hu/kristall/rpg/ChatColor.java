package hu.kristall.rpg;

public enum ChatColor {
	
	BLACK("\u001b[30m", '0'), 		//0
	DARK_BLUE("\u001b[34m", '1'), 	//1
	DARK_GREEN("\u001b[32m", '2'),	//2
	DARK_AQUA("\u001b[36m", '3'),	//3
	DARK_RED("\u001b[31m", '4'),		//4
	DARK_PURPLE("\u001b[35m", '5'),	//5
	GOLD("\u001b[33m", '6'),			//6
	GRAY("\u001b[37m", '7'),			//7
	DARK_GRAY("\u001b[90m", '8'),	//8
	BLUE("\u001b[94m", '9'),			//9
	
	GREEN("\u001b[92m", 'a'),		//a
	AQUA("\u001b[96m", 'b'),			//b
	RED("\u001b[91m", 'c'),			//c
	PINK("\u001b[95m", 'd'),			//d
	YELLOW("\u001b[93m", 'e'),		//e
	WHITE("\u001b[97m", 'f'),		//f
	
	RESET("\u001b[0m", 'r');			//r
	
	
	public final String colorCode;
	public final String ansiCode;
	public final char colorChar;
	
	ChatColor(String ansi, char code) {
		this.ansiCode = ansi;
		this.colorChar = code;
		this.colorCode = new String(new char[]{'§',colorChar});
	}
	
	@Override
	public String toString() {
		return colorCode;
	}
	
	public static ChatColor findByChar(char c) {
		if(c > 47 && c< 58) {
			return values()[c - 48];
		}
		if(c > 96 && c< 103) {
			return values()[c - 87];
		}
		if(c > 64 && c < 71) {
			return values()[c - 55];
		}
		if(c == 'r') {
			return RESET;
		}
		return null;
	}
	
}

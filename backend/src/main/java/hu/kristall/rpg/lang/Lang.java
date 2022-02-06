package hu.kristall.rpg.lang;

import hu.kristall.rpg.lang.component.ArgumentComponent;
import hu.kristall.rpg.lang.component.MessageComponent;
import hu.kristall.rpg.lang.component.QueryComponent;
import hu.kristall.rpg.lang.component.StaticComponent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Lang {
	
	private Map<String, Message> messageMap;
	
	private Lang(Map<String, Message> map) {
		messageMap = new HashMap<>(map);
	}
	
	public Lang() {
		messageMap = new HashMap<>();
		
	}
	
	public Lang deepCopy() {
		Lang l = new Lang(messageMap);
		l.messageMap.putAll(messageMap);
		return l;
	}
	
	//NO NEED $ SIGN
	private Message parseMessage(String inputText) {
		
		char[] input = inputText.toCharArray();
		LinkedList<MessageComponent> components = new LinkedList<>();
		
		boolean escapemode = false;
		int currentStart = 0;
		
		for(int i = 0; i < input.length; ++i) {
			if(escapemode) {
				if(input[i] == '}') {
					if(currentStart == i) {
						throw new IllegalArgumentException("escape sequence cannot be empty");
					}
					//handle escaped content START
					String escapedContent = new String(input, currentStart, i-currentStart);
					//System.out.println("escaped content: \""+escapedContent+'"');
					try {
						components.add(new ArgumentComponent(Integer.parseInt(escapedContent)));
					}
					catch (NumberFormatException e) {
						components.add(new QueryComponent(escapedContent));
					}
					//handle escaped content END
					
					//end escape mode
					currentStart = i+1;
					escapemode = false;
					continue;
				}
			}
			else {
				if(input[i] == '&') {
					input[i] = '§';
					//System.out.println("REPLACED & SIGN");
					continue;
				}
				/*if(input[i] == '$') {*/ //OLD STUFF
				if(input[i] == '{') {
					//System.out.println("found $");
					/*if(input.length-1 == i) { //OLD STUFF
						//System.out.println("end of text");
						//end of text. no need to add here, remaing text is added at the end
						continue;
					}*/
					/*if(input[i+1] == '{') {*/ //DELETED
					//System.out.println("Lang::entering escape mode");
					if(currentStart != i) {
						String staticText = new String(input, currentStart, i-currentStart);
						//System.out.println("adding static \""+staticText+'"');
						components.add(new StaticComponent(staticText));
					}
					escapemode = true;
					//currentStart = i+2; //CHANGED
					currentStart = i+1;
					//System.out.println(input[i]);
					//System.out.println("currentstart was set to "+currentStart);
					
					//++i;
					continue;
					//} DELETED
				}
			}
		}
		
		//no need to do fancy build stuff, simple string is ok
		if(components.size() == 0) {
			return new StaticMessage(new String(input), inputText);
		}
		
		if(escapemode)
			throw new IllegalArgumentException("syntax error: unclosed escape sequence");
		
		//add remaining content
		if(currentStart != input.length) {
			components.add(new StaticComponent(new String(input, currentStart, input.length-currentStart)));
		}
		
		return new SimpleMessage(inputText, components);
	}
	
	//NEED $ SIGN
	/*
	private Message parseMessage(String inputText) {
		
		char[] input = inputText.toCharArray();
		LinkedList<MessageComponent> components = new LinkedList<>();
		
		boolean escapemode = false;
		int currentStart = 0;
		
		for(int i = 0; i < input.length; ++i) {
			if(escapemode) {
				if(input[i] == '}') {
					if(currentStart == i) {
						throw new IllegalArgumentException("escape sequence cannot be empty");
					}
					//handle escaped content START
					String escapedContent = new String(input, currentStart, i-currentStart);
					//System.out.println("escaped content: \""+escapedContent+'"');
					try {
						components.add(new ArgumentComponent(Integer.parseInt(escapedContent)));
					}
					catch (NumberFormatException e) {
						components.add(new QueryComponent(escapedContent));
					}
					//handle escaped content END
					
					//end escape mode
					currentStart = i+1;
					escapemode = false;
					continue;
				}
			}
			else {
				if(input[i] == '&') {
					input[i] = '§';
					continue;
				}
				if(input[i] == '$') {
					//System.out.println("found $");
					if(input.length-1 == i) {
						//System.out.println("end of text");
						//end of text. no need to add here, remaing text is added at the end
						continue;
					}
					if(input[i+1] == '{') {
						//System.out.println("Lang::entering escape mode");
						if(currentStart != i) {
							String staticText = new String(input, currentStart, i-currentStart);
							//System.out.println("adding static \""+staticText+'"');
							components.add(new StaticComponent(staticText));
						}
						escapemode = true;
						currentStart = i+2;
						//System.out.println(input[i]);
						//System.out.println("currentstart was set to "+currentStart);
						
						++i;
						continue;
					}
				}
			}
		}
		
		//no need to do fancy build stuff, simple string is ok
		if(components.size() == 0) {
			return new StaticMessage(new String(input));
		}
		
		if(escapemode)
			throw new IllegalArgumentException("syntax error: unclosed escape sequence");
		
		//add remaining content
		if(currentStart != input.length) {
			components.add(new StaticComponent(new String(input, currentStart, input.length-currentStart)));
		}
		
		return new SimpleMessage(inputText, components);
	}
	*/
	
	public void addMessage(String key, Message message) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(message);
		
		messageMap.put(key, message);
	}
	
	public Message getRawMessage(String messageID) {
		return messageMap.get(messageID);
	}
	
	public String getMessage(String messageID, String... args) {
		Objects.requireNonNull(messageID);
		Objects.requireNonNull(args);
		
		Message msg = messageMap.get(messageID);
		if(msg == null) {
			return missingMessage(messageID);
		}
		try {
			return msg.compute(this, args);
		}
		catch (Throwable e) {
			return missingMessage(messageID);
		}
	}
	
	public String missingMessage(String messageID) {
		if(messageID == null)
			return "{null}";
		char[] source = messageID.toCharArray();
		char[] target = new char[messageID.length()+2];
		target[0] = '{';
		System.arraycopy(source, 0, target, 1, source.length);
		target[target.length-1] = '}';
		return new String(target);
	}
	
	public void loadConfigFromJar(String jarPath) throws IOException {
		try(BufferedReader r = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(jarPath))))) {
			loadConfig(r);
		}
	}
	
	public void loadConfig(BufferedReader reader) throws IOException {
		String s = null;
		while(true) {
			s = reader.readLine();
			if(s == null)
				break;
			char[] value = s.toCharArray();
			int firstChar = 0;
			while((firstChar < value.length) && (value[firstChar] == ' ' || value[firstChar] == '\t')) {
				++firstChar;
			}
			
			//ha a sor végére ért és nem talált nem-whitespace karaktert (vagy 0 hosszú volt a sor)
			if(firstChar == value.length)
				continue;
			
			//ha az első karakter '#', azaz a sor egy komment
			if(value[firstChar] == '#')
				continue;
			int index = firstChar;
			for(; index < s.length(); ++index) {
				if(value[index] == '=')
					break;
			}
			//ha nem talált '=' karaktert
			if(index == value.length) {
				continue;
			}
			messageMap.put(new String(value, firstChar, index-firstChar), parseMessage(new String(value, index+1, value.length-index-1)));
		}
	}
	
}
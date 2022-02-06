package hu.kristall.rpg.auth.interfaces;

import com.google.gson.JsonObject;

public interface AuthMethod {
	
	boolean authenticate(JsonObject object);
	String type();
	
}

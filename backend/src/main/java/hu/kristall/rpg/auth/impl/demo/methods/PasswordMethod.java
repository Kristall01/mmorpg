package hu.kristall.rpg.auth.impl.demo.methods;

import com.google.gson.JsonObject;
import hu.kristall.rpg.auth.interfaces.AuthMethod;

public class PasswordMethod implements AuthMethod {
	
	private final String password;
	
	public PasswordMethod(String password) {
		this.password = password;
	}
	
	@Override
	public boolean authenticate(JsonObject object) {
		return password.contentEquals(object.getAsJsonObject().get("password").getAsString());
	}
	
	@Override
	public String type() {
		return "password";
	}
}

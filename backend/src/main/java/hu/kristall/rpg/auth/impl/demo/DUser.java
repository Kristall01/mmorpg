package hu.kristall.rpg.auth.impl.demo;

import hu.kristall.rpg.auth.interfaces.AuthMethod;
import hu.kristall.rpg.auth.interfaces.User;
import hu.kristall.rpg.auth.interfaces.UserManager;

import java.util.HashMap;

public class DUser implements User {
	
	private final UserManager manager;
	private HashMap<String, AuthMethod> authMethods = new HashMap<>();
	private String name;
	
	public DUser(UserManager manager, String name) {
		this.manager = manager;
		this.name = name;
	}
	
	@Override
	public AuthMethod getAuthMethod(String type) {
		return authMethods.get(type);
	}
	
	@Override
	public boolean addAuthMethod(AuthMethod method) {
		return this.authMethods.putIfAbsent(method.type(), method) == null;
	}
	
	@Override
	public UserManager getManager() {
		return manager;
	}
	
}

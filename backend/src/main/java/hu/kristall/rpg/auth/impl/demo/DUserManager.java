package hu.kristall.rpg.auth.impl.demo;

import hu.kristall.rpg.auth.impl.demo.methods.PasswordMethod;
import hu.kristall.rpg.auth.interfaces.AuthMethod;
import hu.kristall.rpg.auth.interfaces.User;
import hu.kristall.rpg.auth.interfaces.UserManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DUserManager implements UserManager {
	
	private HashMap<String, DUser> users = new HashMap<>();
	
	public DUser registerUser(String name, Iterable<AuthMethod> methods) {
		if(users.containsKey(name)) {
			return null;
		}
		DUser u = new DUser(this, name);
		for (AuthMethod method : methods) {
			u.addAuthMethod(method);
		}
		users.put(name, u);
		return u;
	}
	
	@Override
	public DUser registerUser(String name) {
		return this.registerUser(name, Collections.emptyList());
	}
	
	public DUser registerUser(String name, String password) {
		return this.registerUser(name, List.of(new PasswordMethod(password)));
	}
	
	@Override
	public User getUserByName(String name) {
		return users.get(name);
	}
	
}

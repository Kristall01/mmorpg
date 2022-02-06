package hu.kristall.rpg.auth.interfaces;

import java.util.Collections;

public interface UserManager {

	User registerUser(String name, Iterable<AuthMethod> methods);
	
	default User registerUser(String name) {
		return registerUser(name, Collections.emptyList());
	}
	
	User getUserByName(String name);

}

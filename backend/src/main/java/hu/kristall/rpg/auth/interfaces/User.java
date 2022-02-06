package hu.kristall.rpg.auth.interfaces;

public interface User {

	AuthMethod getAuthMethod(String type);
	boolean addAuthMethod(AuthMethod method);
	UserManager getManager();

}

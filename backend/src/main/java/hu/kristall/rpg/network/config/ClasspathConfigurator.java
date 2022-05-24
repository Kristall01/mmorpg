package hu.kristall.rpg.network.config;

import io.javalin.http.staticfiles.Location;
import io.javalin.http.staticfiles.StaticFileConfig;

public class ClasspathConfigurator implements HostConfigurator {
	
	private String classpathDir;
	
	public ClasspathConfigurator(String classpathDir) {
		this.classpathDir = classpathDir;
	}
	
	@Override
	public void accept(StaticFileConfig config) {
		config.hostedPath = "/";
		config.directory = classpathDir;
		config.location = Location.CLASSPATH;
		config.precompress = false;
	}
	
}

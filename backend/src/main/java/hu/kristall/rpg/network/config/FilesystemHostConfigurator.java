package hu.kristall.rpg.network.config;

import io.javalin.http.staticfiles.Location;
import io.javalin.http.staticfiles.StaticFileConfig;

public class FilesystemHostConfigurator implements HostConfigurator {
	
	private String directory;
	
	public FilesystemHostConfigurator(String directory) {
		this.directory = directory;
	}
	
	@Override
	public void accept(StaticFileConfig config) {
		config.precompress = false;
		config.hostedPath = "/";
		config.location = Location.EXTERNAL;
		config.directory = directory;
	}
	
}

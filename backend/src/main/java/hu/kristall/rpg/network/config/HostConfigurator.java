package hu.kristall.rpg.network.config;

import io.javalin.http.staticfiles.StaticFileConfig;

import java.util.function.Consumer;

public interface HostConfigurator extends Consumer<StaticFileConfig> {

}

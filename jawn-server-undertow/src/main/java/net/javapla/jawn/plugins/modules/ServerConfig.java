package net.javapla.jawn.plugins.modules;


import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import net.javapla.jawn.core.ApplicationConfig;
import net.javapla.jawn.core.api.ApplicationBootstrap;
import net.javapla.jawn.core.server.Server;
import net.javapla.jawn.server.undertow.UndertowServer2;

public class ServerConfig implements ApplicationBootstrap {

    @Override
    public void bootstrap(ApplicationConfig config) {
        config.registerModules(new AbstractModule() {

            @Override
            protected void configure() {
                bind(Server.class).to(UndertowServer2.class).in(Singleton.class);
            }
            
        });
    }

    @Override
    public void destroy() { }

}

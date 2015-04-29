package net.javapla.jawn.core;

import net.javapla.jawn.core.i18n.Lang;
import net.javapla.jawn.core.parsers.JsonMapperProvider;
import net.javapla.jawn.core.parsers.ParserEngineManager;
import net.javapla.jawn.core.parsers.ParserEngineManagerImpl;
import net.javapla.jawn.core.parsers.XmlMapperProvider;
import net.javapla.jawn.core.templates.TemplateEngineOrchestrator;
import net.javapla.jawn.core.templates.TemplateEngineOrchestratorImpl;
import net.javapla.jawn.core.templates.config.ConfigurationReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class CoreModule extends AbstractModule {
    
    private final PropertiesImpl properties;
    private final Router router;
    
    CoreModule(PropertiesImpl properties, Router router) {
        this.properties = properties;
        this.router = router;
    }

    @Override
    protected void configure() {
        bind(PropertiesImpl.class).toInstance(properties);
        bind(Lang.class).in(Singleton.class);
        
        bind(Router.class).toInstance(router);
        
        // Marshallers
        bind(ObjectMapper.class).toProvider(JsonMapperProvider.class).in(Singleton.class);
        bind(XmlMapper.class).toProvider(XmlMapperProvider.class).in(Singleton.class);
        
        bind(ConfigurationReader.class).in(Singleton.class);
        
        bind(TemplateEngineOrchestrator.class).to(TemplateEngineOrchestratorImpl.class).in(Singleton.class);
        bind(ParserEngineManager.class).to(ParserEngineManagerImpl.class).in(Singleton.class);
        bind(ResponseRunner.class).in(Singleton.class);
        
        bind(Context.class).to(ContextImpl.class);
        
        bind(ControllerActionInvoker.class).in(Singleton.class);
        bind(FilterChainEnd.class).in(Singleton.class);
        
        bind(FrameworkEngine.class).in(Singleton.class);
    }

    
}
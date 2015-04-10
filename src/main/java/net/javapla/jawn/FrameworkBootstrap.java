package net.javapla.jawn;

import java.util.ArrayList;
import java.util.List;

import net.javapla.jawn.application.FrameworkConfig;
import net.javapla.jawn.exceptions.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

public class FrameworkBootstrap {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    
    final PropertiesImpl properties;
    Injector injector;

    private FrameworkConfig config;

    public FrameworkBootstrap(PropertiesImpl conf) {
        properties = conf;
    }
    
    public synchronized void boot() {
        if (injector != null) throw new RuntimeException(FrameworkBootstrap.class.getSimpleName() + " already initialised");
        
        long startupTime = System.currentTimeMillis();
        
        // Read all the configuration from the user
        ConfigApp appConfig = new ConfigApp();
        Filters filters = new Filters();
        Router router = new Router(filters);
        
        config = readConfiguration(appConfig, router, filters);
        
        Injector localInjector = initInjector(Lists.newArrayList(appConfig.getRegisteredModules()), router);
        router.compileRoutes(localInjector);
        
        injector = localInjector;
        
        FrameworkEngine engine = injector.getInstance(FrameworkEngine.class);
        engine.onFrameworkStartup();
        
        logger.info("Bootstrap of framework started in " + (System.currentTimeMillis() - startupTime) + " ms");
    }
    
    public synchronized void shutdown() {
        if (config != null) {
            config.destroy();
        }
        if (injector != null) {
            FrameworkEngine engine = injector.getInstance(FrameworkEngine.class);
            engine.onFrameworkShutdown();
            injector = null;
            engine = null;
        }
    }
    
    public Injector getInjector() {
        return injector;
    }

    private Injector initInjector(final List<AbstractModule> userModules, Router router) {
        // this class is a part of the server project
        // configure all the needed dependencies for the server
        // this includes injecting templatemanager
        
        List<AbstractModule> combinedModules = new ArrayList<>();
        
        combinedModules.add(new AbstractModule() {
            @Override
            protected void configure() {
                // TODO readfilters, create router, compile router, remove filters from bind
//                bind(Router.class).toInstance(createRouter(readFilters()));
//                bind(Filters.class).toInstance(readFilters());
                bind(Router.class).toInstance(router);//.in(Singleton.class);
            }
        });
        combinedModules.add(new CoreModule(properties));
        
        combinedModules.addAll(userModules);
        
        
        
        return Guice.createInjector(Stage.PRODUCTION, combinedModules);
    }
    
    /*private Filters readFilters() {
        Filters filters = new Filters();
        
        String filterConfigClassName = "app.config.FilterConfig";
        // try to read custom routes provided by user
        try {
            IFilterConfig filterConfig = DynamicClassFactory.createInstance(filterConfigClassName, IFilterConfig.class, false);
            filterConfig.init(filters);
            logger.debug("Instantiated filters from: " + filterConfigClassName);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch(ConfigurationException e){
            throw  e;
        } catch (Exception e) {
            logger.debug("Did not find any filters: " + getCauseMessage(e));
        }
        
        return filters;
    }*/
    
    /*private Router createRouter(Filters filters, Router router) {
//        Router router = new Router(filters, injector);//injector.getInstance(Router.class);
        
        String routeConfigClassName = "app.config.RouteConfig";
        // try to read custom routes provided by user
        try {
            IRouteConfig localRouteConfig = DynamicClassFactory.createInstance(routeConfigClassName, IRouteConfig.class, false);
            localRouteConfig.init(router);
            logger.debug("Loaded routes from: " + routeConfigClassName);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch(ConfigurationException e){
            throw  e;
        } catch (Exception e) {
            logger.debug("Did not find custom routes. Going with built in defaults: " + getCauseMessage(e));
        }
        
        router.compileRoutes(injector);
        
        return router;
    }*/
    
    private FrameworkConfig readConfiguration(ConfigApp configuration, Router router, Filters filters) {
        
        String configClassName = "app.config.ApplicationConfiguration";//TODO reconsider naming
        
        try {
            FrameworkConfig localConfig = DynamicClassFactory.createInstance(configClassName, FrameworkConfig.class, false);
            
            localConfig.bootstrap(configuration);
            localConfig.filters(filters);
            localConfig.router(router);
            
            logger.debug("Loaded configuration from: " + configClassName);
            return localConfig;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch(ConfigurationException e){
            throw  e;
        } catch (Exception e) {
            logger.debug("Did not find custom configuration. Going with built in defaults: " + getCauseMessage(e));
        }
        
        return null;
    }
    
  //TODO: refactor to some util class. This is stolen...ehrr... borrowed from Apache ExceptionUtils
    static String getCauseMessage(Throwable throwable) {
        List<Throwable> list = new ArrayList<Throwable>();
        while (throwable != null && list.contains(throwable) == false) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list.get(0).getMessage();
    }
}

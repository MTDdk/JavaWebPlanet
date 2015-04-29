package net.javapla.jawn.server;


import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import net.javapla.jawn.core.Context;
import net.javapla.jawn.core.FrameworkBootstrap;
import net.javapla.jawn.core.FrameworkEngine;
import net.javapla.jawn.core.ModeHelper;
import net.javapla.jawn.core.PropertiesImpl;
import net.javapla.jawn.core.Context.Internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * @author MTD
 */
public class RequestDispatcher implements Filter {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    
    private ServletContext servletContext;
    private Set<String> exclusions = new TreeSet<String>();

    private Injector injector;

    private PropertiesImpl properties;

    private FrameworkBootstrap bootstrapper;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        properties = new PropertiesImpl(ModeHelper.determineModeFromSystem());
        
        this.servletContext = filterConfig.getServletContext();
        
        bootstrapper = new FrameworkBootstrap(properties/*, controllerRegistry.getModules()*/);
        bootstrapper.boot();
        injector = bootstrapper.getInjector();
        
        findExclusionPaths();
        
        // either the encoding was set by the user, or we default
        //TODO make encoding configurable
//        String enc = appContext.getAsString(AppContext.ENCODING);
//        if (enc == null) {
//            enc = Constants.ENCODING;
//            appContext.set(AppContext.ENCODING, enc);
//        }
//        logger.debug("Setting encoding: " + enc);
        
//        root_controller = filterConfig.getInitParameter("root_controller");
        logger.info("ActiveWeb: starting the app in environment: " + properties.getMode());//Configuration.getEnv());
    }

    private void findExclusionPaths() {
        // Let other handlers deal with folders that do not reside in the WEB-INF or META-INF
        Set<String> resourcePaths = servletContext.getResourcePaths("/");
        resourcePaths.removeIf( path -> path.contains("-INF") || path.contains("-inf"));
        for (String path : resourcePaths) {
            if (path.charAt(0) != '/')
                path = '/' + path;
            if (path.charAt(path.length()-1) == '/')
                path = path.substring(0, path.length()-1);// remove the last slash
            exclusions.add(path);
        }
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        try {
            doFilter((HttpServletRequest) req, (HttpServletResponse) resp, chain);
        } catch (ClassCastException e) {
            // ought not be possible
            throw new ServletException("non-HTTP request or response", e);
        }
    }
    
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)  throws ServletException, IOException {
//        try {

            String path = /*request.getRequestURI();*/request.getServletPath();
            
            // redirect if the path ends with a slash - URLS cannot end with a slash - they should not do such a thing - why would they?
            if (redirectUrlEndingWithSlash(path, response)) return;

            //MTD: filtering resources
            if (filteringResources(request, response, chain, path)) return;
            
            Context.Internal context = (Internal) injector.getInstance(Context.class);
            context.init(servletContext, request, response);
            FrameworkEngine engine = injector.getInstance(FrameworkEngine.class);
            engine.runRequest(context);
            
            /*String format = null;
            String uri;
            // look for any format in the request
            if(path.contains(".")){
                uri = path.substring(0, path.lastIndexOf('.'));
                format = path.substring(path.lastIndexOf('.') + 1);
            }else{
                uri = path;
            }
            
            
            //TODO first do this language extraction IF custom route not found
            String language = findLanguagePrefix(uri, injector.getInstance(Lang.class));
                
            //MTD: we first look for a language prefix and strip the URI if it is found
            if (!StringUtil.blank(language)) {
                uri = uri.substring(language.length() +1 );
//                if (uri.isEmpty()) uri = "/";
            } else {
                language = injector.getInstance(Lang.class).getDefaultLanguage();//defaultLanguage;
            }
            
            if (StringUtil.blank(uri)) {
                uri = "/";//different servlet implementations, damn.
            }*/
            
            
            // Try to look up the route
//            Route route = router.getRoute(HttpMethod.getMethod(request), uri);

            /*if (route != null) {
                //TODO consider if it should be possible to ignore routes
//                if(route.ignores(path)) {
//                    chain.doFilter(request, response); // let someone else handle this
//                    logger.debug("URI ignored: " + path);
//                    return;
//                }

                
                Context context = injector.getInstance(Context.class);
                context.init(servletContext, request, response);
                context.setRouteInformation(route, format, language, uri);
                
                // run filters
                ControllerResponse controllerResponse = route.getFilterChain().before(context);
                
                ResponseRunner runner = injector.getInstance(ResponseRunner.class);
                runner.run(context, controllerResponsecontext.getNewControllerResponse());
                
                route.getFilterChain().after(context);
            } else {
                //TODO: theoretically this will never happen, because if the route was not excluded, the router.recognize() would throw some kind
                // of exception, leading to the a system error page.
                logger.warn("No matching route for servlet path: " + request.getServletPath() + ", passing down to container.");
                chain.doFilter(request, response);//let it fall through
            }
        } catch (CompilationException e) {
            renderSystemError(request, response, e);
//        } catch (ClassLoadException e) {
//            renderSystemError(request, response, "/system/404", useDefaultLayoutForErrors() ? getDefaultLayout():null, 404, e);
//        }catch (ActionNotFoundException e) {
//            renderSystemError(request, response, "/system/404", useDefaultLayoutForErrors() ? getDefaultLayout():null, 404, e);
        }catch(ClassLoadException | ActionNotFoundException |ViewMissingException | RouteException e){
            renderSystemError(request, response, "/system/404", properties.get(Constants.Params.defaultLayout.name()), 404, e);
//        }catch(RouteException e){
//            renderSystemError(request, response, "/system/404", useDefaultLayoutForErrors() ? getDefaultLayout():null, 404, e);
        }catch(ViewException e){
            renderSystemError(request, response, "/system/error", properties.get(Constants.Params.defaultLayout.name()), 500, e);
        }catch (Throwable e) {
            renderSystemError(request, response, e);
        }finally {
//           Context.clear();
//            List<String> connectionsRemaining = DB.getCurrrentConnectionNames();
//            if(connectionsRemaining.size() != 0){
//                logger.warn("CONNECTION LEAK DETECTED ... and AVERTED!!! You left connections opened:"
//                        + connectionsRemaining + ". ActiveWeb is closing all active connections for you...");
//                DB.closeAllConnections();
//            }
        }*/
    }
    
    
    
    private boolean redirectUrlEndingWithSlash(String path, HttpServletResponse response) throws IOException {
        if (path.length() > 1 && path.endsWith("/")) { 
            response.sendRedirect(path.substring(0, path.length()-1));
            return true;
        }
        return false;
    }

    /**/


    /**
     * A problem arises when the server tries to serve a resource, which does not start with an excluded path,
     * and the server just sees it as a route to handle itself, when it actually ought to send it up the chain
     * 
     * @author MTD
     * @return true, if a filtering has happened and nothing else should be done by this current dispatcher
     */
    private boolean filteringResources(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, String path) throws ServletException, IOException {
        String translated = translateResource(path);
        if (translated != null) {
//            logger.debug("URI excluded: {}", path);
            
            HttpServletRequest r = req;
            if (path.length() != translated.length()) {//!path.startsWith(translated)) {
//                int indexOfExcluded = path.indexOf(translated);
//                String t = path.substring(indexOfExcluded);
                r = createServletRequest(req, translated);
//                logger.debug(".. and translated -> {}", translated);
            }
            
            
            // setting default headers for static files
            resp.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=86400"); // 24 hours
            File file = new File(servletContext.getRealPath(translated));
            if (file.canRead())
                resp.setHeader(HttpHeaders.ETAG, String.valueOf(file.lastModified()));
            
            chain.doFilter(r, resp);
            return true;
        }
        return false;
    }
    
    /**
     * Creates a new HttpServletRequest object.
     * Useful, as we cannot modify an existing ServletRequest.
     * Used when resources needs to have the {controller} stripped from the servletPath.
     * 
     * @author MTD
     */
    private HttpServletRequest createServletRequest(HttpServletRequest req, String translatedPath) {
        return new HttpServletRequestWrapper(req){
            @Override
            public String getServletPath() {
                return translatedPath;
            }
        };
    }

    /**
     * Instead of just verifying if a path contains an exclusion, the method returns the exclusion.
     * This takes care of some resources being prepended with the language prefix.
     * Like:
     *      http://localhost/da/images/something.jpg
     * should be the same as
     *      http://localhost/en/images/something.jpg,
     *      http://localhost/images/something.jpg
     * @author MTD
     */
    private String translateResource(String servletPath) {
        // TODO look at the necessity of this line. Its purpose is to serve single files not in a folder of the root webapp - like favicon.ico
        if (exclusions.contains(servletPath))
            return servletPath;
        
        int start = 0, end = servletPath.indexOf('/',1);
        String segment;
        while (end > -1) { // fail fast
            segment = servletPath.substring(start, end);
            for (String exclusion : exclusions) {
                if (segment.equals( exclusion )) {
                    return servletPath.substring(start);
                }
            }
            start = end;
            end = servletPath.indexOf('/',start+1);
        }
        return null;
    }
    
    
    @Override
    public void destroy() {
        bootstrapper.shutdown();
//        appBootstrap.destroy(appContext);
    }
}

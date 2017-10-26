package net.javapla.jawn.core.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.inject.Inject;
import com.google.inject.Injector;

import net.javapla.jawn.core.Controller;
import net.javapla.jawn.core.Result;
import net.javapla.jawn.core.exceptions.ControllerException;
import net.javapla.jawn.core.exceptions.WebException;
import net.javapla.jawn.core.http.Context;
import net.javapla.jawn.core.routes.Route;
import net.javapla.jawn.core.routes.Route.ResponseFunction;

public class ActionInvoker implements ResponseFunction {

    private final Injector injector;
    
    @Inject
    public ActionInvoker(Injector injector) {
        
        //README this might be necessary if the overhead of creating controllers on runtime exceeds the memory of holding the controller on the Route
        // if we want to reload the controller, this is a good time to do it
        
        // OVERRIDE README: We cannot(!) cache the concrete instances, as they are very much stateful
        // (they hold different Context AND produce different Response)
        // As long as Context OR Response is held within the controller, a controller cannot be reused
//            if (!useCachedController) {
//                controller = loadController(route.getController().getClass().getName(), useCachedController);
//            } else {
//                controller = route.getController();
//            }
            
        this.injector = injector;
    }
    
    @Override
    public Result handle(Context context) {
        return executeAction(context);
    }
    
    public final Result executeAction(final Context context) {
        final Route route = context.getRoute();

        try {
            //final Controller controller = injector.getInstance(route.getController());
            //controller.init(context);
            final Controller controller = DynamicClassFactory.createInstance(route.getController());
            injectControllerWithContext(controller, context, injector);

            //find the method name and run it
            /*final String methodName = route.getAction();
            for (Method method : controller.getClass().getMethods()) {
                //if (method.getReturnType().equals(Response.class)) 
                if (methodName.equals( method.getName())) {
                    
                    // handle if the return type is 'void'
                    //if (method.getReturnType().equals(Void.TYPE))
                    //if (response == null) throw 404Exception();
                    
                    method.invoke(controller);//route.getController());
                    return controller.getControllerResponse();
                }
            }
            throw new ControllerException(String.format("Action name (%s) not found in controller (%s)", route.getAction(), route.getController().getSimpleName()));
            */
            /*return (Response) *//*route.getActionMethod()*/route.getActionMethod().invoke(controller);
            return controller.getControllerResult();
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                if (e.getCause() instanceof  WebException) {
                    throw (WebException)e.getCause();                
                } else if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException)e.getCause();
                }
                throw new ControllerException(e.getCause());
            }
            throw new ControllerException(e);
        } catch(WebException e) {
            throw e;
        } catch(Exception e) {
            throw new ControllerException(e);
        }
    }
    
    
    private final static void injectControllerWithContext(final Controller controller, final Context context, final Injector injector) {
        controller.init(context, injector);
        
        //Inject dependencies
        injector.injectMembers(controller);
    }
    
    public final static boolean isAllowedAction(Class<? extends Controller> controller, String actionMethodName) {
        for (Method method : controller.getMethods()) {
            if (actionMethodName.equals(method.getName())) {
                return true;
            }
        }
        return false;
    }
    
}

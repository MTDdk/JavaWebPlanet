package net.javapla.jawn;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Simple servlet that allows to run the framework inside a servlet container
 * @author MTD
 *
 */
//TODO make it a part of the server project
public class FrameworkServletDispatcher extends HttpServlet {

    private static final long serialVersionUID = -7219362539542656401L;

    Injector injector;
    FrameworkEngine framework;
    
    @Inject
    public FrameworkServletDispatcher(Injector injector, FrameworkEngine framework) {
        this.injector = injector;
        this.framework = framework;
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        
        Context context = injector.getInstance(Context.class);
        context.init(servletContext, req, resp);
        
        framework.runRequest(context);
    }
}

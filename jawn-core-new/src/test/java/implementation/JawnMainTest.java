package implementation;

import net.javapla.jawn.core.Jawn;
import net.javapla.jawn.core.Results;
import net.javapla.jawn.core.Status;
import net.javapla.jawn.core.filters.LogRequestTimingFilter;
import net.javapla.jawn.core.server.ServerConfig.PERFORMANCE;

public class JawnMainTest extends Jawn {
    
    {
        server().serverPerformance(PERFORMANCE.MINIMUM);
        
        get("/t", Results.text("holaaaa5588")).before(SomeRandomClass::before);
        get("/xml", Results.xml("<xml>teeeest</xml>"));
        get("/json", Results.json("{\"key\":\"teeeest\"}"));
        
        get("/test", ctx -> Results.text("teeeest :: " + ctx.param("dd").orElse("")).status(201));
        post("/test/{dd}", ctx -> Results.text("teeeest :: " + ctx.param("dd").orElse("")).status(Status.ALREADY_REPORTED));
        
        get("/", Results.html()/*.path("system")*//*.template("404").layout(null)*/);
        
        filter(LogRequestTimingFilter.class);
    }

    public static void main(String[] args) {
        run(JawnMainTest.class, args);
    }

}

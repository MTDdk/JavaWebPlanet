package net.javapla.jawn.core;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class RouteFilterPopulatorTest {

    @Test
    public void emptyBefores() {
        Jawn j = new Jawn();
        j.get("/", Results.noContent());
        j.buildRoutes().forEach(r -> assertThat(r.before()).isNull());
    }
    
    @Test
    public void emptyAfters() {
        Jawn j = new Jawn();
        j.get("/", Results.noContent());
        j.buildRoutes().forEach(r -> assertThat(r.after()).isNull());
    }

    @Test
    public void orderingOfFilters() {
        // filters should be executed in correct order
        // filter1.before -> filter2.before -> filter3.before -> filter3.after -> filter2.after -> filter1.after
        
        Jawn j = new Jawn();
        Context context = mock(Context.class);
        Result result = Results.noContent();
        long[] executionOrder = new long[7];
        
        j.get("/", result).before(c -> {executionOrder[0] = System.nanoTime();});
        j.filter(new Route.Filter() {
            @Override
            public void before(Context context) {
                executionOrder[1] = System.nanoTime();
            }
            
            @Override
            public void after(Context context, Result result) {
                executionOrder[6] = System.nanoTime();
            }
        });
        j.filter(new Route.Filter() {
            @Override
            public void before(Context context) {
                executionOrder[2] = System.nanoTime();
            }
            
            @Override
            public void after(Context context, Result result) {
                executionOrder[5] = System.nanoTime();
            }
        });
        j.filter(new Route.Filter() {
            @Override
            public void before(Context context) {
                executionOrder[3] = System.nanoTime();
            }
            
            @Override
            public void after(Context context, Result result) {
                executionOrder[4] = System.nanoTime();
            }
        });
        
        // execute
        j.buildRoutes().forEach(r -> {
            r.handle(context);
        });
        
        // by asking if the executionOrder is ordered
        // the before/after relation should be established according to
        // how the ordering of the filters *should* be 
        assertThat(executionOrder).asList().isOrdered();
    }

}

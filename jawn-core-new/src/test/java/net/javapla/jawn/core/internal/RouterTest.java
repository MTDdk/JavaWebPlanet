package net.javapla.jawn.core.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.javapla.jawn.core.HttpMethod;
import net.javapla.jawn.core.Route;
import net.javapla.jawn.core.Up;

public class RouterTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void simple() {
        List<Route> routes = Arrays.asList(new Route.Builder(HttpMethod.GET).path("/first").build());
        
        Router router = new Router(routes);
        
        Route route = router.retrieve(HttpMethod.GET, "/first");
        assertThat(route).isNotNull();
    }
    
    @Test
    public void samePathNotSameMethod() {
        List<Route> routes = Arrays.asList(new Route.Builder(HttpMethod.GET).path("/first").build());
        
        Router router = new Router(routes);
        
        try {
            router.retrieve(HttpMethod.POST, "/first");
            fail();
        } catch (Up.RouteFoundWithDifferentMethod e) {}
        
        try {
            router.retrieve(HttpMethod.PUT, "/first");
            fail();
        } catch (Up.RouteFoundWithDifferentMethod e) {}
        
        try {
            router.retrieve(HttpMethod.DELETE, "/first");
            fail();
        } catch (Up.RouteFoundWithDifferentMethod e) {}
    }
    
     @Test
     public void headShouldAlwaysWork() {
         List<Route> routes = Arrays.asList(
             new Route.Builder(HttpMethod.GET).path("/first").build(),
             new Route.Builder(HttpMethod.DELETE).path("/delete").build()
         );
         
         Router router = new Router(routes);
         
         Route route = router.retrieve(HttpMethod.GET, "/first");
         assertThat(route).isNotNull();
         
         route = router.retrieve(HttpMethod.HEAD, "/first");
         assertThat(route).isNotNull();
         
         route = router.retrieve(HttpMethod.HEAD, "/delete");
         assertThat(route).isNotNull();
     }
     
     @Test
     public void wildcards() {
         List<Route> routes = Arrays.asList(
             new Route.Builder(HttpMethod.GET).path("/first/{something}").build(),
             new Route.Builder(HttpMethod.GET).path("/second/{something}/more").build()
         );
         
         Router router = new Router(routes);
         
         Route route = router.retrieve(HttpMethod.GET, "/first/cookie");
         assertThat(route).isNotNull();
         
         route = router.retrieve(HttpMethod.GET, "/second/cookie/more");
         assertThat(route).isNotNull();
     }

     @Test
     public void routeWithCorrectMethod() {
         List<Route> routes = Arrays.asList(
             new Route.Builder(HttpMethod.GET).path("/first/{some}").build(),
             new Route.Builder(HttpMethod.DELETE).path("/delete").build(),
             new Route.Builder(HttpMethod.POST).path("/post/{test}").build()
         );
         
         Router router = new Router(routes);
         
         try {
             router.retrieve(HttpMethod.POST, "/first/some");
             fail();
         } catch (Up.RouteFoundWithDifferentMethod e) {}
         
         try {
             router.retrieve(HttpMethod.DELETE, "/first");
             fail();
         } catch (Up.RouteMissing e) {}
         
         Route route = router.retrieve(HttpMethod.POST, "/post/concrete");
         assertThat(route.method()).isEqualTo(HttpMethod.POST);
         
         route = router.retrieve(HttpMethod.DELETE, "/delete");
         assertThat(route.method()).isEqualTo(HttpMethod.DELETE);
     }
}

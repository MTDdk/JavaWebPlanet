package net.javapla.jawn.core;

import static com.google.common.truth.Truth.assertThat;

import java.util.Collections;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ViewTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void simpleView() {
        View view = Results.view();
        assertThat(view.renderable().isPresent()).isTrue();
        assertThat(view.renderable().get()).isEqualTo(view);
        assertThat(view.model()).isEmpty();
    }

    @Test
    public void withModel() {
        String key = "key";
        String value = "simple";
        View view = Results.view().put(key, value);
        assertThat(view.model()).hasSize(1);
        assertThat(view.model()).containsExactly(key, value);
    }
    
    @Test
    public void putMap() {
        String key = "key";
        String value = "simple";
        View view = Results.view().put(Collections.singletonMap(key, value)).put("k", "v");
        assertThat(view.model()).hasSize(2);
        assertThat(view.model()).containsExactly(key, value, "k", "v");
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void settingRenderable_should_fail() {
        Results.view().renderable(new Object());
    }
    
    @Test
    public void inheritFromResult() {
        View view = View.from(Results.notFound());
        
        assertThat(view.status).isEqualTo(Status.NOT_FOUND);
        assertThat(view.contentType()).isEqualTo(MediaType.HTML);
    }
    
    @Test
    public void overwriteTypes() {
        View view = View.from(Results.json("").status(300).charset("UTF-16"));
        
        assertThat(view.status).isEqualTo(Status.valueOf(300));
        assertThat(view.contentType).isEqualTo(MediaType.HTML);
        assertThat(view.charset.name()).isEqualTo("UTF-16");
    }
}

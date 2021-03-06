package net.javapla.jawn.core.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD })
@Repeatable(Path.Paths.class)
public @interface Path { 
    
    String value();
    
    
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD })
    @interface Paths {
        Path[] value();
    }
}

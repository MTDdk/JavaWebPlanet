package net.javapla.jawn.core.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import net.javapla.jawn.core.Config;
import net.javapla.jawn.core.Modes;
import net.javapla.jawn.core.Config.ParseOptions;
import net.javapla.jawn.core.util.CollectionUtil;

public class ConfigImplTest {
    
    static Config config;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        config = ConfigImpl.parse(Modes.DEV, "jawn_defaults_test.properties");
    }
    

    @Test
    public void parse() {
        assertThat(config).isNotNull();
        assertThat(config.isDev()).isTrue();
        assertThat(config.isTest()).isFalse();
        assertThat(config.isProd()).isFalse();
    }

    @Test
    public void parseWithoutExtention() {
        Config config = ConfigImpl.parse(Modes.DEV, "jawn_defaults_test");
        assertThat(config).isNotNull();
    }
    
    @Test
    public void parseWithOptions() {
        Config config = ConfigImpl.parse(Modes.DEV, "jawn_defaults_test", ParseOptions.defaults().charset(StandardCharsets.ISO_8859_1).classLoader(this.getClass().getClassLoader()));
        assertThat(config).isNotNull();
        assertThat(config.getInt("application.someint")).isEqualTo(200);
    }
    
    @Test
    public void parseMap() {
        Config config = ConfigImpl.parse(Modes.DEV, CollectionUtil.map("key1","value1","dev.key2","value2","prod.key3","value3"));
        
        assertThat(config.isDev()).isTrue();
        assertThat(config.get("key1")).isNotNull();
        assertThat(config.get("key2")).isEqualTo("value2");
        assertThat(config.get("key3")).isNull(); // Because Modes.DEV
    }
    
    @Test
    public void readValue() {
        assertThat(config.getOptionally("application.charset").isPresent()).isTrue();
        assertThat(config.get("application.charset")).isEqualTo("UTF-8");
        assertThat(config.getOrDie("application.charset")).isEqualTo("UTF-8");
    }
    
    @Test
    public void failOnMissingKey() {
        assertThrows(RuntimeException.class, () -> config.getOrDie("application.bogus"));
        assertThrows(RuntimeException.class, () -> config.getDurationOrDie("application.bogus"));
        assertThrows(RuntimeException.class, () -> config.getDurationOrDie("application.bogus", TimeUnit.SECONDS));
        assertThrows(RuntimeException.class, () -> config.getBooleanOrDie("application.bogus"));
    }
    
    @Test
    public void readIntValue() {
        assertThat(config.getIntOptionally("application.someint").isPresent()).isTrue();
        assertThat(config.getIntOptionally("application.someint").get()).isEqualTo(200);
        assertThat(config.getInt("application.someint")).isEqualTo(200);
        assertThat(config.getIntOrDie("application.someint"));
        
        assertThrows(RuntimeException.class, () -> config.getIntOrDie("no"));
    }
    
    @Test
    public void readLongValue() {
        assertThat(config.getLongOptionally("application.somelong").isPresent()).isTrue();
        assertThat(config.getLongOptionally("application.somelong").get()).isEqualTo(4_599_100_100l);
        assertThat(config.getLong("application.somelong")).isEqualTo(4_599_100_100l);
        assertThat(config.getLongOrDie("application.somelong"));
        
        assertThrows(RuntimeException.class, () -> config.getLongOrDie("no"));
    }
    
    @Test
    public void readBoolValue() {
        assertThat(config.getBooleanOptionally("application.somebool").isPresent()).isTrue();
        assertThat(config.getBoolean("application.somebool")).isFalse();
    }
    
    @Test
    public void readDurationValue() {
        assertThat(config.getDuration("application.sometime")).isEqualTo(Duration.ofMinutes(5));
        assertThat(config.getDurationOptionally("application.sometime").get()).isEqualTo(Duration.ofMinutes(5));
        
        assertThat(config.getDuration("application.sometime2")).isEqualTo(Duration.ofSeconds(10));
    }
    
    @Test
    public void readDurationAsAnotherUnit() {
        assertThat(config.getDuration("application.sometime", TimeUnit.SECONDS)).isEqualTo(Duration.ofMinutes(5).getSeconds());
        assertThat(config.getDuration("application.sometime", TimeUnit.MINUTES)).isEqualTo(5);
        assertThat(config.getDuration("application.sometime2", TimeUnit.SECONDS)).isEqualTo(10);
    }
    
    @Test
    public void merge() {
        ConfigImpl conf1 = ConfigImpl.parse(CollectionUtil.map("test", "test"));
        ConfigImpl conf2 = ConfigImpl.parse(CollectionUtil.map("test", "overridden", 
                                                           "extra", "3"));
        
        Config merge = conf1.merge(conf2);
        
        
        assertThat(merge.get("test")).isEqualTo("overridden");
        assertThat(merge.get("extra")).isNotNull();
        
        // nothing should have been overridden
        assertThat(conf1.get("test")).isEqualTo("test");
        assertThat(conf1.get("extra")).isNull();
        assertThat(conf2.get("test")).isEqualTo("overridden");
    }
    
    @Test
    public void mergeWithDifferentModes() {
        ConfigImpl conf1 = ConfigImpl.empty();
        ConfigImpl conf2 = ConfigImpl.empty(Modes.TEST);
        
        assertThat(conf1.getMode()).isEqualTo(Modes.DEV);
        
        Config merge = conf1.merge(conf2);
        
        assertThat(conf1.getMode()).isEqualTo(Modes.DEV);
        assertThat(merge.getMode()).isEqualTo(conf2.getMode());
    }
    
    @Test
    public void hasPartialPath() {
        assertThat(config.hasPath("application")).isTrue();
        assertThat(config.hasPath("database.local")).isTrue();
    }
    
    @Test
    public void hasPartialPath_with_differentModes() {
        assertThat(config.hasPath("database.local.extra")).isTrue();
        assertThat(config.get("database.local.extra")).isEqualTo("extravalue");
        
        assertThat(ConfigImpl.parse(Modes.TEST, "jawn_defaults_test.properties").get("database.local.extra")).isEqualTo("extravaluetest");
    }
    
    @Test
    public void keysOfPartialPath() {
        assertThat(config.keysOf("database")).hasSize(7);
        assertThat(config.keysOf("database.local")).hasSize(4);
    }
    
}

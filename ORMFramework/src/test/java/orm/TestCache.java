package orm;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import orm.annotations.Entity;
import orm.annotations.PrimaryKey;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

public class TestCache {

    @Data
    @AllArgsConstructor
    @Entity
    public static class Person {
        @PrimaryKey
        protected Long id;
        protected String firstname;
        protected String lastname;
    }

    private ICache cache;
    private Person person1;
    private Person person2;

    @BeforeEach
    public void setUp(){
        cache = new Cache();
        person1 = new Person(1L,"firstname1","lastname1");
        person2 = new Person(2L,"firstname2","lastname2");
    }

    @Test
    public void testContainsTrue(){
        cache.put(Person.class,person1);
        assertTrue(cache.contains(Person.class,1L));
    }

    @Test
    public void testContainsFalse(){
        assertFalse(cache.contains(Person.class,1L));
    }

    @Test
    public void testHasChangedTrue(){
        cache.put(Person.class,person1);
        person1.setFirstname("other");
        try {
            assertTrue(cache.hasChanged(Person.class,person1));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHasChangedFalse(){
        cache.put(Person.class,person1);
        try {
            assertFalse(cache.hasChanged(Person.class,person1));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHasChangedTrueWhenNotContains(){
        try {
            assertTrue(cache.hasChanged(Person.class,person1));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

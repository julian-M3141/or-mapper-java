package orm;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface ICache {
    Map<Object, Object> getCache(Class c);

    Object get(Class<?> c, Object pk);

    void put(Object o);

    boolean contains(Class<?> c, Object pk);

    boolean contains(Object o);

    void remove(Object o);

    boolean hasChanged(Object o) throws InvocationTargetException, IllegalAccessException;

    boolean hasChanged(Class<?> c, Object o) throws InvocationTargetException, IllegalAccessException;
}

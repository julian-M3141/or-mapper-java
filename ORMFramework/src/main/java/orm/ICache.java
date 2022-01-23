package orm;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Provides an Interface for a simple Cache
 */
public interface ICache {

    /**
     * returns a map with the primary keys as key and the objects as values
     * @param c the class of the objects
     * @return returns a map with the primary keys as key and the objects as values
     */
    Map<Object, Object> getCache(Class c);

    /**
     * returns the object for the given primary key.
     * @param c the class of the object.
     * @param pk the primary key of the object.
     * @return returns the requested object.
     */
    Object get(Class<?> c, Object pk);

    /**
     * adds an object to the cache.
     * @param o the object which will be added to the cache.
     */
    void put(Class<?> c,Object o);

    /**
     * checks whether the primary key is already in the cache.
     * @param c the requested class.
     * @param pk the pk of the requested object.
     * @return returns if the object with the primary key is in the cache.
     */
    boolean contains(Class<?> c, Object pk);

    /**
     * checks whether the object is in the cache.
     * @param o the requested object.
     * @param c the requested class.
     * @return returns if the object is in the cache.
     */
    boolean contains(Object o, Class<?> c);

    /**
     * removes an object from the cache.
     * @param o the object which will be removed.
     */
    void remove(Class<?> c, Object o);

    /**
     * checks whether the object has changed since insert/last update.
     * @param o the object, which will be checked.
     * @return returns true if the object has changed, false otherwise.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    boolean hasChanged(Object o) throws InvocationTargetException, IllegalAccessException;

    /**
     * checks whether the object with the primary key pk has changed since insert/last update.
     * @param c the class of the object.
     * @param o the object, which will be checked.
     * @return returns true if the object has changed, false otherwise.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    boolean hasChanged(Class<?> c, Object o) throws InvocationTargetException, IllegalAccessException;
}

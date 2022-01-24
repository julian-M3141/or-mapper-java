package orm;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


/**
 * Implements the functionality of the ICache Interface, can save, get, delete objects and determine whether an object has changed since last save/select.
 */
public class Cache implements ICache {

    /**
     * This field holds a map with Class as key and a map of Object,Object as values.
     * The submap contains the primary keys as keys and the objects as values.
     */
    private final Map<Class<?>, Map<Object,Object>> cache = new HashMap<>();

    /**
     * This field stores the hashes of the objects for tracking changes.
     */
    private final Map<Class<?>, Map<Object,Integer>> hashes = new HashMap<>();

    /**
     * returns a map with the primary keys as key and the objects as values
     * @param c the class of the objects
     * @return returns a map with the primary keys as key and the objects as values
     */
    @Override
    public Map<Object,Object> getCache(Class c){
        if(!cache.containsKey(c)){
            cache.put(c,new HashMap<>());
        }
        return cache.get(c);
    }

    /**
     * returns the object for the given primary key.
     * @param c the class of the object.
     * @param pk the primary key of the object.
     * @return returns the requested object.
     */
    @Override
    public Object get(Class<?> c, Object pk){
        return (contains(c,pk)) ? getCache(c).get(pk) : null;
    }

    /**
     * adds an object to the cache.
     * @param c the class of the object.
     * @param o the object which will be added to the cache.
     */
    @Override
    public void put(Class<?> c, Object o){
        try {
            if(o != null) {
                getCache(c).put(ORM.getEntity(o).getPrimaryKey().getValue(o), o);
                getHashes(c).put(ORM.getEntity(o).getPrimaryKey().getValue(o),o.hashCode());
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks whether the primary key is already in the cache.
     * @param c the requested class.
     * @param pk the pk of the requested object.
     * @return returns if the object with the primary key is in the cache.
     */
    @Override
    public boolean contains(Class<?> c, Object pk){
        return getCache(c).containsKey(pk);
    }

    /**
     * checks whether the object is in the cache.
     * @param o the requested object.
     * @return returns if the object is in the cache.
     */
    @Override
    public boolean contains(Object o, Class<?> c) {
        try {
            return getCache(c).containsKey(ORM.getEntity(o).getPrimaryKey().getValue(o));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * removes an object from the cache.
     * @param o the object which will be removed.
     */
    @Override
    public void remove(Class<?> c,Object o){
        try {
            getCache(c).remove(ORM.getEntity(o).getPrimaryKey().getValue(o));
            getCache(c).remove(ORM.getEntity(o).getPrimaryKey().getValue(o));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks whether the object has changed since insert/last update.
     * @param o the object, which will be checked.
     * @return returns true if the object has changed, false otherwise.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    public boolean hasChanged(Object o) throws InvocationTargetException, IllegalAccessException {
        return true;
        //too complicate to implement functional clone functionality for recursive relationships (myclass and students)
//        return !(contains(o) && get(o.getClass(),ORM.getEntity(o).getPrimaryKey().getValue(o)).equals(o));
    }

    /**
     * checks whether the object with the primary key pk has changed since insert/last update.
     * @param c the class of the object.
     * @param o the object, which will be checked.
     * @return returns true if the object has changed, false otherwise.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    public boolean hasChanged(Class<?> c, Object o) throws InvocationTargetException, IllegalAccessException {
        return !(contains(o,c) && getHashes(c).get(ORM.getEntity(c).getPrimaryKey().getValue(o)).equals(o.hashCode()));
    }

    private Map<Object, Integer> getHashes(Class<?> c){
        if(!hashes.containsKey(c)){
            hashes.put(c,new HashMap<>());
        }
        return hashes.get(c);
    }

}

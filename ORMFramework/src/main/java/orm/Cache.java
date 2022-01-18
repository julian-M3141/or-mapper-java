package orm;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Cache implements ICache {
    private Map<Class<?>, Map<Object,Object>> cache = new HashMap<>();

    @Override
    public Map<Object,Object> getCache(Class c){
        if(!cache.containsKey(c)){
            cache.put(c,new HashMap<>());
        }
        return cache.get(c);
    }

    @Override
    public Object get(Class<?> c, Object pk){
        return (contains(c,pk)) ? getCache(c).get(pk) : null;
    }

    @Override
    public void put(Object o){
        try {
            if(o != null) {
                getCache(o.getClass()).put(ORM.getEntity(o).getPrimaryKey().getValue(o), clone(o));
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean contains(Class<?> c, Object pk){
        return getCache(c).containsKey(pk);
    }

    @Override
    public boolean contains(Object o) {
        try {
            return getCache(o.getClass()).containsKey(ORM.getEntity(o).getPrimaryKey().getValue(o));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void remove(Object o){
        try {
            getCache(o.getClass()).remove(ORM.getEntity(o).getPrimaryKey().getValue(o));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasChanged(Object o) throws InvocationTargetException, IllegalAccessException {
        // todo deep copy
        System.out.println("after: "+o);
        System.out.println("before "+get(o.getClass(),ORM.getEntity(o).getPrimaryKey().getValue(o)));
        return !(contains(o) && get(o.getClass(),ORM.getEntity(o).getPrimaryKey().getValue(o)).equals(o));
    }

    @Override
    public boolean hasChanged(Class<?> c, Object o) throws InvocationTargetException, IllegalAccessException {
        return !(contains(c,o) && get(c,ORM.getEntity(c).getPrimaryKey().getValue(o)).equals(o));
    }

    public static Object clone(Object o){
        try{
            Object cloned = o.getClass().getConstructor().newInstance();
            Class<?> c = o.getClass();
            do{
                for(var field : c.getDeclaredFields()){
                    field.setAccessible(true);
                    field.set(cloned,field.get(o));
                }
            }while (!(c=c.getSuperclass()).equals(Object.class));
            return cloned;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}

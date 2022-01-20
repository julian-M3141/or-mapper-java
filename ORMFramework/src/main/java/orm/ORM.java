package orm;


import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import orm.metamodel._Entity;
import orm.metamodel._Field;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ORM provides the interface for orm-functionality
 * @author julian
 */
@Log

public class ORM {
    /**
     * This field contains all used Entities
     */
    protected static Map<Class<?>, _Entity> entities = new HashMap<>();

    /**
     * This field contains the connection to the database
     */
    protected static Connection connection = null;

    /**
     * This field contains the cache, all saved and selected items will be stored in the cache
     */
    protected static ICache cache = new Cache();


    /**
     * Sets a new Cache for the ORM class
     * @param cache this parameter will be the new cache. this parameter cannot be null.
     */
    public static void setCache(ICache cache) {
        ORM.cache = Objects.requireNonNullElseGet(cache, Cache::new);
    }


    /**
     * This method sets the connection to the database if not already done and returns it
     * @return the connection to the database
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        //todo get data from a config file
        //todo throw own exception
        if(connection==null){
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                    "testuser", "testpwd");
        }
        return connection;
    }


    /**
     * This method returns the entity for a given Object
     * @param object can either be a member of a class or a metaclass-object
     * @return the entity for the given object
     */
    public static _Entity getEntity(Object object){
        var c = object instanceof Class<?> ? (Class<?>) object : object.getClass();
        if(!entities.containsKey(c)){
            try {
                entities.put(c, new _Entity(c));
            } catch (Exception e) {
                e.printStackTrace();
                //todo throw own exception
            }
        }
        return entities.get(c);
    }


    /**
     * This method saves an Object to the Database
     * @param o The object that will be saved.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    public static void save(Object o) throws InvocationTargetException, IllegalAccessException, SQLException {
        var entity = getEntity(o);
        if(entity==null) return;
        if (!cache.hasChanged(o)){
            return;
        }
        cache.put(o);

        saveHelper(entity,o);
    }

    //to save the fields of superclass

    /**
     * This method saves the Object to the corresponding class.
     * Is mainly used to save the parentclass of an object, as table per type is used.
     * @param t the Class object, which holds the class of the object
     * @param o the object to be saved
     * @param <T> the generic type of the class
     * @throws SQLException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected static <T> void save(Class<T> t,Object o) throws SQLException, InvocationTargetException, IllegalAccessException {
        var entity = getEntity(t);
        if(entity==null) return;
        if (!cache.hasChanged(t,o)){
            //System.out.println("object is in cache: "+o.toString());
            return;
        }
        cache.put(o);
        saveHelper(entity,o);
    }


    /**
     * This method creates the SQL command for the insert, prepares all parameters,
     * saves the superclass, if the superclass is not Object.class
     * and executes the query
     * @param entity the Entity.class for the object
     * @param o the object, that will be saved
     * @throws SQLException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static void saveHelper(_Entity entity, Object o) throws SQLException, InvocationTargetException, IllegalAccessException {


        var insert = entity.getSQL_INSERT() + " " + entity.getSQL_UPDATE();


        //create prepared statement
        var statement = getConnection().prepareStatement(insert);
        int n = 1;
        int numFields = entity.getFields().length - entity.getExternals().size();
        int subtraction = 1;
        for(var field : entity.getFields()){
            Object value = null;
            if(field.isFK()){
                var fk = field.getValue(o);
                if(field.isOneToMany() || field.isManyToMany()){
                    continue;
                }
                save(fk);
                value = getEntity(fk).getPrimaryKey().getValue(fk);
            }else{
                value = field.getValue(o);
            }

            statement.setObject(n++,value);
            if(field.isPK()){
                subtraction++;
            }
            statement.setObject(field.isPK() ? 2*numFields : n + numFields-subtraction,value);
        }

        if(!entity.getMember().getSuperclass().equals(Object.class)){
            save(entity.getMember().getSuperclass(), o);
        }

        System.out.println(insert);

        statement.execute();
        statement.close();
        for (var external : entity.getExternals()){
            saveExternals(o, (Collection<Object>) external.getValue(o),external);
        }
    }


    /**
     * This method saves oneToMany and ManyToMany relationships
     * @param o the object, which holds the
     * @param list a collection with the external relationships
     * @param field the metafield of the external relationship
     * @throws SQLException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected static void saveExternals(Object o,Collection<Object> list,_Field field) throws SQLException, InvocationTargetException, IllegalAccessException {
        if(field.isOneToMany()){
            for (var value : list){
                save(value);
            }
        }else{
            var entity = getEntity(o);
            String command = "INSERT INTO "+field.getAssignmentTable()+" ("+field.getColumnName()+","+field.getRemoteColumnName()+") VALUES (?,?) ON CONFLICT DO NOTHING;";

            var stmt = getConnection().prepareStatement(command);
            var pk = entity.getPrimaryKey().getValue(o);
            for(var value : list){
                var outerEntity = getEntity(value);
                stmt.setObject(1,pk);
                stmt.setObject(2,outerEntity.getPrimaryKey().getValue(value));
                stmt.execute();
            }
            stmt.close();
        }
    }


    //creates object from primary key
    protected static <T> Object createObject(Class<T> c, Object pk) {
        if(cache.contains(c,pk)){
            //System.out.println("get object from cache: " + c.getName() + " - "+pk);
            return cache.get(c,pk);
        }
        var entity = getEntity(c);

        var sql = entity.getSQL_SELECT();

        Object obj = null;
        try {
            //create statement
            var statement = getConnection().prepareStatement(sql);

            //set primary key
            statement.setObject(1,pk);
            //execute query
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                obj = createObjectFromResultSet(c,pk,rs);
            }


            cache.put(obj);
            return obj;


        } catch (SQLException e) {
            e.printStackTrace();
            //todo throw own exception
        }

        return null;
    }


    //creates object from resultset from sql query
    private static Object createObjectFromResultSet(Class<?> c,Object pk, ResultSet rs) {
        if(cache.contains(c,pk)){
            //System.out.println("Get object form cache: "+c.getName()+ " - " + pk);
            return cache.get(c,pk);
        }
        //create new object
        Object obj = null;
        try {
            obj = c.getConstructor().newInstance();
            for(_Field f : getEntity(c).getFields()){
                Object val = null;
                if(f.isFK()){
                    if(f.isManyToMany() || f.isOneToMany()){
//                        val = getExternals(f.getColumnType(),pk,f);
                        continue;
                    }else /*Many To One or One to One*/ {
                        val = get(f.getFieldType(),rs.getObject(f.getColumnName()));
                    }
                }else{
                    val = rs.getObject(f.getColumnName());
                }
                f.setValue(obj,val);
            }

            //inheritance
            //if superclass is not object class
            //get superclass object from db
            //and copy fields from childobject to parentobject
            if(!getEntity(c).getMember().getSuperclass().equals(Object.class)){
                var parent = createObject(c.getSuperclass(),pk);
//                System.out.println("Parent : " +parent);
                for(_Field f : getEntity(c.getSuperclass()).getFields()){
                    f.setValue(obj,f.getValue(parent));
                }
            }

        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }


        //System.out.println("put object into cache " + obj);
        cache.put(obj);
        for(var field : getEntity(c).getExternals()){
            try {
                field.setValue(obj,getExternals(field.getColumnType(),pk,field));
            } catch (InvocationTargetException | IllegalAccessException | SQLException | NoSuchMethodException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    private static <T> Collection<T> getExternals(Class<T> t, Object pk, _Field field) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(field.isManyToMany()){
//            System.out.println("many to many");
            Collection<T> list = new ArrayList<T>();
            String sql = new StringBuilder("SELECT ")
                    .append(field.getColumnName())
                    .append(", ")
                    .append(field.getRemoteColumnName())
                    .append(" FROM ")
                    .append(field.getAssignmentTable())
                    .append(" WHERE ")
                    .append(field.getColumnName())
                    .append(" = ?;")
                    .toString();
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setObject(1,pk);
            System.out.println(sql);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                list.add((T) createObject(t,resultSet.getObject(field.getRemoteColumnName())));
            }
            return list;

        }else{
            Collection<T> list = new ArrayList<T>();
            String sql = "SELECT " +
                    Arrays.stream(getEntity(t).getFields())
                            .filter(x -> !(x.isManyToMany()) || x.isOneToMany())
                            .map(_Field::getColumnName)
                            .collect(Collectors.joining(", ")) +
                    " FROM " +
                    getEntity(t).getTableName() +
                    " WHERE " +
                    field.getRemoteColumnName() +
                    "=?;";
            System.out.println(sql);
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setObject(1,pk);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                list.add((T) createObjectFromResultSet(t,resultSet.getObject(getEntity(t).getPrimaryKey().getColumnName()),resultSet));
            }
            return list;
        }
    }

    public static <T> T get(Class<T> c, Object pk){
        return (T) createObject(c,pk);
    }


    public static QueryObject query(String command) throws SQLException {
        return new QueryObject(command);
    }

    public static QueryObject get(Class<?> c){
        var tablename = getEntity(c).getTableName();
        var sql = "SELECT * FROM "+tablename;
        return new QueryObject(c,sql);
    }

    //package private
    static <T> T executeQueryOne(Class<T> c, QueryObject queryObject) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(queryObject.getSQL());
        ResultSet resultSet = stmt.executeQuery();

        if(resultSet.next()){
            //get the primary key, else object cannot be initiated
            Object pk = resultSet.getObject(getEntity(c).getPrimaryKey().getColumnName());
            return (T) createObjectFromResultSet(c,pk,resultSet);
        }

        return null;
    }

    static <T> List<T> executeQueryMany(Class<T> c, QueryObject queryObject) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(queryObject.getSQL());
        ResultSet resultSet = stmt.executeQuery();
        List<T> result = new ArrayList<>();
        while(resultSet.next()){
            //get the primary key, else object cannot be initiated
            Object pk = resultSet.getObject(getEntity(c).getPrimaryKey().getColumnName());
            result.add((T) createObjectFromResultSet(c,pk,resultSet));
        }

        return result;
    }


    public static <T> void execute(Class<T> c, QueryObject queryObject) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(queryObject.getSQL());
        stmt.execute();
    }
}

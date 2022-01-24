package orm;


import lombok.extern.java.Log;
import orm.metamodel._Entity;
import orm.metamodel._Field;

import java.io.IOException;
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
     * This field determines whether to show the sql queries
     */
    protected static boolean showSql = true;

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
            String url = "";
            String user = "";
            String password = "";
            try {
                url = ConfigurationHandler.getConfigPropertyValue("dburl");
                user = ConfigurationHandler.getConfigPropertyValue("dbuser");
                password = ConfigurationHandler.getConfigPropertyValue("dbpassword");
                showSql = Boolean.parseBoolean(ConfigurationHandler.getConfigPropertyValue("show-sql"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection = DriverManager.getConnection(url,
                    user , password);
        }
        return connection;
    }

    /**
     * Logs the provided text to standard output
     * @param text the text that should be logged
     */
    private static void log(String text){
        if(showSql){
            log.info(text);
        }
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
        if (!cache.hasChanged(entity.getMember(),o)){
            return;
        }
        cache.put(entity.getMember(),o);

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
            return;
        }
        cache.put(t,o);
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

        log(insert);

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
        var entity = getEntity(o);
        var pk = entity.getPrimaryKey().getValue(o);

        if(field.isOneToMany()){
            var type = field.getColumnType();
            var outer_entity = getEntity(type);
            if(field.isNullable()){
                var sql = "UPDATE "+outer_entity.getTableName()
                        + " SET " + field.getRemoteColumnName()
                        + "=NULL WHERE " + field.getRemoteColumnName() + "=?;";
                var stmt = getConnection().prepareStatement(sql);
                stmt.setObject(1,pk);
                log(sql);
                stmt.execute();
                stmt.close();
            }
            var sql = "UPDATE "+outer_entity.getTableName()
                    + " SET " + field.getRemoteColumnName()
                    + "=? WHERE " + outer_entity.getPrimaryKey().getColumnName() + "=?;";
            var stmt = getConnection().prepareStatement(sql);
            for (var value : list){
                save(value);
                log(sql);
                stmt.setObject(1,pk);
                stmt.setObject(2,outer_entity.getPrimaryKey().getValue(value));
                stmt.execute();
            }
        }else{

            String deleteCommand = "DELETE FROM "+field.getAssignmentTable()+ " WHERE "+field.getColumnName()+"=?;";
            var deleteStatement = getConnection().prepareStatement(deleteCommand);
            deleteStatement.setObject(1,pk);
            log(deleteCommand);
            deleteStatement.execute();
            deleteStatement.close();

            String command = "INSERT INTO "+field.getAssignmentTable()+" ("+field.getColumnName()+","+field.getRemoteColumnName()+") VALUES (?,?) ON CONFLICT DO NOTHING;";

            var stmt = getConnection().prepareStatement(command);
            for(var value : list){
                save(value);
                log(command);
                var outerEntity = getEntity(value);
                stmt.setObject(1,pk);
                stmt.setObject(2,outerEntity.getPrimaryKey().getValue(value));
                stmt.execute();
            }
            stmt.close();
        }
    }


    //creates object from primary key

    /**
     * executes a select query using the primary key and creates an object from the selected data
     * @param c the class of the object, that will be returned.
     * @param pk the primary key of the object.
     * @param <T> the generic type for the object.
     * @return returns the requested object from the database or null if it not exists
     */
    protected static <T> Object createObject(Class<T> c, Object pk) {
        if(cache.contains(c,pk)){
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
            log(sql);
            //execute query
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                obj = createObjectFromResultSet(c,pk,rs);
            }


            cache.put(c,obj);
            return obj;


        } catch (SQLException e) {
            e.printStackTrace();
            //todo throw own exception
        }

        return null;
    }



    /**
     * creates object from resultset from sql query using the metadata from Entity object.
     * If the class is inherited from another class, it will set all fields from the parent class
     * @param c the class of the object, that will be returned.
     * @param pk the primary key of the object, that will be returned. Is needed for cache look-up
     * @param rs the resultset with the data, from which the object will be created
     * @return returns the requested object
     */
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
                for(_Field f : getEntity(c.getSuperclass()).getFields()){
                    f.setValue(obj,f.getValue(parent));
                }
            }

        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }


        cache.put(c,obj);
        for(var field : getEntity(c).getExternals()){
            try {
                field.setValue(obj,getExternals(field.getColumnType(),pk,field));
            } catch (InvocationTargetException | IllegalAccessException | SQLException | NoSuchMethodException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * Selects a many to many or one to many relationship as a Collection from the database to the given primary key
     * @param t the class of the external field
     * @param pk the primary key of the object
     * @param field the external field
     * @param <T> the generic Type for the external field
     * @return a Collection of the external field
     * @throws SQLException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private static <T> Collection<T> getExternals(Class<T> t, Object pk, _Field field) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(field.isManyToMany()){
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
            log(sql);
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
            log(sql);
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setObject(1,pk);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                list.add((T) createObjectFromResultSet(t,resultSet.getObject(getEntity(t).getPrimaryKey().getColumnName()),resultSet));
            }
            return list;
        }
    }

    /**
     * Selects a record from the database and creates an object from the data.
     * @param c the class of the object, that will be returned.
     * @param pk the primary key of the object.
     * @param <T> the generic type of the returned object.
     * @return the requested object from the database.
     */
    public static <T> T get(Class<T> c, Object pk){
        return (T) createObject(c,pk);
    }

    /**
     * Deletes the object from the database.
     * @param o the object, that will be deleted.
     * @throws SQLException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void delete(Object o) throws SQLException, InvocationTargetException, IllegalAccessException {
        var entity = getEntity(o);
        deleteHelper(entity,o);
    }

    /**
     * Deletes the requested Object from the database.
     * Is required for deleting the record from the parent table.
     * @param c the class of the object that will be deleted.
     * @param o the object that will be deleted.
     * @throws SQLException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static void delete(Class<?> c, Object o) throws SQLException, InvocationTargetException, IllegalAccessException {
        var entity = getEntity(c);
        deleteHelper(entity,o);
    }

    /**
     * private helper function to delete an object from a database.
     * @param entity the _Entity object of the to be deleted object.
     * @param o the object that will be deleted.
     * @throws SQLException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
     private static void deleteHelper(_Entity entity, Object o) throws SQLException, InvocationTargetException, IllegalAccessException {

         // get slq statement
         var sql = entity.getSQL_DELETE();

         //delete record
         PreparedStatement stmt = getConnection().prepareStatement(sql);
         stmt.setObject(1,entity.getPrimaryKey().getValue(o));
         log(sql);
         stmt.execute();

         //delete parent if inheritance
         if(!entity.getMember().getSuperclass().equals(Object.class)){
             var tmp = entity.getMember();
             while(!tmp.getSuperclass().equals(Object.class)){
                 tmp = tmp.getSuperclass();
                 delete(tmp,o);
             }
         }
     }


    /**
     * Creates a new Query object with the given sql command.
     * The query can be executed with the execute method, if there is
     * no return value, executeQueryMany if there should be many return values
     * and executeQueryOne, if there should be only one return value
     * @param command the sql command which will be executed.
     * @return returns a new QueryObject
     * @throws SQLException
     */
    public static QueryObject query(String command) throws SQLException {
        return new QueryObject(command);
    }

    /**
     * creates a new QueryObject for fluent API for selecting an object from the database
     * @param c the Class of the to be selected object
     * @return a new QueryObject
     */
    public static QueryObject get(Class<?> c){
        final var tablename = getEntity(c).getTableName();
        if(c.getSuperclass().equals(Object.class)){
            var sql = "SELECT * FROM "+tablename;
            return new QueryObject(c,sql);
        }
        //inheritance requires more complex query cosntruction
        var tmp = c;
        var id = getEntity(c).getPrimaryKey().getColumnName();
        var fields = Arrays.stream(getEntity(c).getFields())
                .map(x -> tablename+"."+x.getColumnName())
                .collect(Collectors.toList());
        String joins  = "";

        // get joins and field names for superclasses
        while(!tmp.getSuperclass().equals(Object.class)){
            tmp = tmp.getSuperclass();
            var entity = getEntity(tmp);
            fields.addAll(Arrays.stream(entity.getFields())
                    .filter(x -> !x.isPK())
                    .map(x -> entity.getTableName()+"."+x.getColumnName())
                    .collect(Collectors.toList()));
            joins += " INNER JOIN "+entity.getTableName() + " ON "+tablename+"."+id+"="+entity.getTableName()+"."+entity.getPrimaryKey().getName();
        }

        var sql = "SELECT "+ String.join(", ", fields)+" FROM "+tablename+joins;
        return new QueryObject(c,sql);
    }

    //package private

    /**
     * Executes a sql query and creates a new Object to the given class.
     * @param c the class of the object which will be returned.
     * @param queryObject the QueryObject, which holds the sql command.
     * @param <T> the generic type for the class.
     * @return returns the requested object.
     * @throws SQLException
     */
    static <T> T executeQueryOne(Class<T> c, QueryObject queryObject) throws SQLException {
        log(queryObject.getSQL());
        PreparedStatement stmt = getConnection().prepareStatement(queryObject.getSQL());
        ResultSet resultSet = stmt.executeQuery();

        if(resultSet.next()){
            //get the primary key, else object cannot be initiated
            Object pk = resultSet.getObject(getEntity(c).getPrimaryKey().getColumnName());
            return (T) createObjectFromResultSet(c,pk,resultSet);
        }

        return null;
    }

    /**
     * Executes a sql query and creates a new list of Objects to the given class.
     * @param c the class of the list which will be returned.
     * @param queryObject the QueryObject, which holds the sql command.
     * @param <T> the generic type for the class.
     * @return returns a list of the requested object.
     * @throws SQLException
     */
    static <T> List<T> executeQueryMany(Class<T> c, QueryObject queryObject) throws SQLException {
        log(queryObject.getSQL());
        PreparedStatement stmt = getConnection().prepareStatement(queryObject.getSQL());
        ResultSet resultSet = stmt.executeQuery();
        List<T> result = new ArrayList<>();
        while(resultSet.next()){
            //get the primary key, else object cannot be initiated
            Object pk = resultSet.getObject(getEntity(c).getPrimaryKey().getColumnName());
            result.add((T) createObjectFromResultSet(c,pk,resultSet));
        }
        stmt.close();

        return result;
    }


    /**
     * Executes a sql query.
     * @param c the class on which the query is executed on.
     * @param queryObject the QueryObject, which holds the sql command.
     * @param <T> the generic type for the class.
     * @throws SQLException
     */
    static <T> void execute(Class<T> c, QueryObject queryObject) throws SQLException {
        log(queryObject.getSQL());
        PreparedStatement stmt = getConnection().prepareStatement(queryObject.getSQL());
        stmt.execute();
        stmt.close();
    }

    /**
     * returns the number of records in a table
     * @param queryObject the QueryObject, which holds the sql command.
     * @return returns the number of records as int
     * @throws SQLException
     */
    static int count(QueryObject queryObject) throws SQLException {
        log(queryObject.getSQL());
        PreparedStatement stmt = getConnection().prepareStatement(queryObject.getSQL());
        ResultSet resultSet = stmt.executeQuery();

        if(resultSet.next()){
            //get the primary key, else object cannot be initiated
            int count = resultSet.getInt(1);
            return count;
        }
        return -1;
    }
}

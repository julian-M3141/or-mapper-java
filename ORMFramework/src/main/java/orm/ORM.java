package orm;


import orm.metamodel._Entity;
import orm.metamodel._Field;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ORM {
    protected static Map<Class<?>, _Entity> entities = new HashMap<>();

    protected static Connection connection = null;

    protected static ICache cache = new Cache();

    public static Connection getConnection() throws SQLException {
        //todo get data from a config file
        if(connection==null){
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                    "testuser", "testpwd");
        }
        return connection;
    }


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


    public static void save(Object o) throws InvocationTargetException, IllegalAccessException, SQLException {
        var entity = getEntity(o);
        if(entity==null) return;
        if (!cache.hasChanged(o)){
            //System.out.println("object is in cache and did not change: "+o.toString());
            return;
        }
        cache.put(o);

        saveHelper(entity,o);
    }

    //to save the fields of superclass
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
            saveExternals(o, (Collection) external.getValue(o),external);
        }
    }


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
                obj = createObject(c,pk,rs);
            }

            //inheritance
            //if superclass is not object class
            //get superclass object from db
            //and copy fields from childobject to parentobject
            if(!entity.getMember().getSuperclass().equals(Object.class)){
                var parent = createObject(c.getSuperclass(),pk);
                for(_Field f : getEntity(parent).getFields()){
                    f.setValue(obj,f.getValue(parent));
                }
            }
            return obj;


        } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            //todo throw own exception
        }

        return null;
    }


    //creates object from resultset from sql query
    private static Object createObject(Class<?> c, Object pk, ResultSet rs) {
        //create new object
        Object obj = null;
        try {
            obj = c.getConstructor().newInstance();
            for(_Field f : getEntity(c).getFields()){
                Object val = null;
                if(f.isFK()){
                    if(f.isManyToMany() || f.isOneToMany()){
                        //todo later val = getExternals(f);
                    }else /*Many To One*/ {
                        val = get(f.getFieldType(),rs.getObject(f.getColumnName()));
                    }
                }else{
                    val = rs.getObject(f.getColumnName());
                }
                f.setValue(obj,val);
            }
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static <T> Collection<T> getExternals(Class<T> t, Object pk, _Field field){
        if(field.isManyToMany()){
            StringBuilder sql = new StringBuilder("select")
                    .append(" from ")
                    .append(field.getAssignmentTable())
                    .append(" where ")
                    .append(field.getColumnName())
                    .append(" = ?");
        }
        StringBuilder sql = new StringBuilder("select values from students where fk = ?");
        Iterable<T> list = new ArrayList<>();


        return null;
    }

    public static <T> T get(Class<T> c, Object pk){
        return (T) createObject(c,pk);
    }


    public static Object castToSuperclass(Object object){
        return object.getClass().getSuperclass().cast(object);
    }
}

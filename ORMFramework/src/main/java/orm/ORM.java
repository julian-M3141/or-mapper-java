package orm;

import orm.metamodel._Entity;
import orm.metamodel._Field;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ORM {
    protected static Map<Class<?>, _Entity> entities = new HashMap<>();

    protected static Connection connection = null;

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

    //todo also update
    public static void save(Object o) throws InvocationTargetException, IllegalAccessException, SQLException {
        var entity = getEntity(o);
        if(entity==null) return;

        var insert = entity.getSQL_INSERT() + " " + entity.getSQL_UPDATE();


        //create prepared statement
        var statement = getConnection().prepareStatement(insert);

        int n = 1;
        int numFields = entity.getFields().length;
        for(var field : entity.getFields()){
            Object value = null;
            if(field.isFK()){
                var fk = field.getValue(o);
                save(fk);
                value = getEntity(fk).getPrimaryKey().getValue(fk);
            }else{
                value = field.getValue(o);
            }
            statement.setObject(n++,value);
            statement.setObject(field.isPK() ? 2*numFields : n + numFields-1,value);
        }

        if(!entity.getMember().getSuperclass().equals(Object.class)){
            save(entity.getMember().getSuperclass(), o);
        }

        System.out.println(insert);

        statement.execute();
        statement.close();
    }

    protected static <T> void save(Class<T> t,Object o) throws SQLException, InvocationTargetException, IllegalAccessException {
        var entity = getEntity(t);
        if(entity==null) return;

        var insert = entity.getSQL_INSERT() + " " + entity.getSQL_UPDATE();


        //create prepared statement
        var statement = getConnection().prepareStatement(insert);

        int n = 1;
        int numFields = entity.getFields().length;
        for(var field : entity.getFields()){
            if(field.isFK()){
                var fk = field.getValue(o);
                save(fk);
                statement.setObject(n++,getEntity(fk).getPrimaryKey().getValue(fk));
                statement.setObject(field.isPK() ? 2*numFields : n + numFields-2,getEntity(fk).getPrimaryKey().getValue(fk));
            }else{
                statement.setObject(n++,field.getValue(o));
                statement.setObject(field.isPK() ? 2*numFields : n + numFields-2,field.getValue(o));
            }
        }

        if(!entity.getMember().getSuperclass().equals(Object.class)){
            save(entity.getMember().getSuperclass(), o);
        }

        System.out.println(insert);

        statement.execute();
        statement.close();
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
                var parent = (T) createObject(c.getSuperclass(),pk);
                for(_Field f : getEntity(c).getFields()){
                    f.setValue(parent,f.getValue(obj));
                }
                return parent;
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
                    val = get(f.getFieldType(),rs.getObject(f.getColumnName()));
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

    public static <T> T get(Class<T> c, Object pk){
        return (T) createObject(c,pk);
    }


    public static Object castToSuperclass(Object object){
        return object.getClass().getSuperclass().cast(object);
    }
}

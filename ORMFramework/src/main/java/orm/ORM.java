package orm;

import orm.metamodel._Entity;
import orm.metamodel._Field;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ORM {
    protected static Map<Class<?>, _Entity> entities;

    protected static Connection connection = DriverManager.getConnection("213");

    protected static _Entity getEntity(Object object) throws NoSuchMethodException {
        var c = object instanceof Class<?> ? (Class) object : object.getClass();

        if(!entities.containsKey(c)){
            entities.put(c, new _Entity(c));
        }

        return entities.get(c);
    }

    //todo also update
    public static void save(Object o) throws InvocationTargetException, IllegalAccessException, SQLException {
        var entity = getEntity(o);

        var insert = new StringBuilder("INSERT INTO " + entity.getTableName() + " (");
        var values = new StringBuilder("");
        var valuenames = new StringBuilder("");

        List<Object> paramlist = new ArrayList<>();

        for(int i=0;i<entity.getFields().length;++i){
            var field = entity.getFields()[i];

            //add separating comma
            if(i>0){
                values.append(", ");
                valuenames.append(", ");
            }

            //add comlumname and value
            valuenames.append(field.getColumnName());
            values.append("?");
            paramlist.add(field.getValue(o));

        }
        insert.append(valuenames).append(") VALUES (").append(values).append(");");

        var statement = connection.prepareStatement(insert.toString());

        int n = 1;
        for(Object i : paramlist) statement.setObject(n++,i);

        statement.execute();
        statement.close();

    }

    protected static Object createObject(Class<?> c, Object pk) throws NoSuchMethodException {
        var entity = getEntity(c);

        var sql = new StringBuilder("SELECT ");

        var statement = connection.prepareStatement();

        return null;
    }

    public static <T> T get(Class<T> c, Object pk){

        return (T) createObject(T,pk);

    }
}

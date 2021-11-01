package orm.metamodel;

import orm.annotations.Entity;
import orm.annotations.Ignore;
import orm.annotations.PrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class _Entity {

    private _Field primaryKey;

    private Class<?> member;

    private String tableName;
    private _Field[] fields;

    private final String SQL_SELECT;
    private final String SQL_INSERT;
    private final String SQL_UPDATE;
    private final String SQL_DELETE;


    //TODO write own Exception for no getter and setter

    public _Entity(Class<?> c) throws Exception {
        //TODO annotate annotations for runtime availability
        var cattr = (Entity) c.getAnnotation(Entity.class);

        //set tablename, either the attribute is set, otherwise take the classname
        if(cattr == null || cattr.tableName() == null || cattr.tableName().equals("")){
            tableName = c.getSimpleName();
        }else{
            tableName = cattr.tableName();
        }
        //set member class
        member = c;


        //get all fields
        List<Field> cfields = new ArrayList<>();
        cfields.addAll(List.of(c.getDeclaredFields()));

        List<_Field> listOfFields = new ArrayList<>();
        //TODO: maybe check if getter and setter are available
        for(var field : cfields){
            if(field.getAnnotation(Ignore.class)!=null) continue;
            var annotations = new __FieldAnnotation(field);
            _Field mField = new _Field(this, field.getName());
            mField.setColumnType(annotations.getColumnType());
            mField.setColumnName(annotations.getColumnName());
            if(annotations.isPK()){
                primaryKey = mField;
                mField.setPK(true);
            }
            if(annotations.isFK()){
                mField.setFK(true);
            }
            mField.setFieldType(field.getType());
            mField.setNullable(annotations.isNullable());
            var nameCapitalizzed = field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
            //todo adapt getter for boolean
            if(field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)){
                mField.setGetter(c.getMethod("is" + nameCapitalizzed));
            }else {
                mField.setGetter(c.getMethod("get" + nameCapitalizzed));
            }
            mField.setSetter(c.getMethod("set" + nameCapitalizzed,field.getType()));
            listOfFields.add(mField);
        }


        //for inheritence set primary key of superclass as pk for child class
        if(!member.getSuperclass().equals(Object.class) && primaryKey == null){
            var pk = getPrimaryKeyFromSuperClass(member);
            primaryKey = pk;
            listOfFields.add(pk);
        }
        fields = listOfFields.toArray(new _Field[0]);

        //set sql commands
        var fieldsForSelectAndInsert = Arrays.stream(fields)
                .map(_Field::getColumnName)
                .collect(Collectors.joining(", "));
        var fieldsForUpdate = Arrays.stream(fields)
                .filter(x -> !x.isPK())
                .map(_Field::getColumnName)
                .map(x -> x+"=?")
                .collect(Collectors.joining(", "));

        SQL_SELECT = "SELECT " + fieldsForSelectAndInsert + " FROM "+tableName+" WHERE "+primaryKey.getColumnName() + "=?;";
        SQL_UPDATE = "ON CONFLICT ("+primaryKey.getColumnName()+") DO UPDATE SET "+fieldsForUpdate + " WHERE " + tableName + "."+primaryKey.getColumnName() + "=?;";
        SQL_INSERT = "INSERT INTO "+tableName+ " ("+fieldsForSelectAndInsert+") VALUES ("+"?, ".repeat(fields.length).substring(0,fields.length*3-2)+")";
        SQL_DELETE = "DELETE FROM "+tableName+" WHERE "+primaryKey.getColumnName() + "=?;";
    }
    public _Field getPrimaryKey() {
        return primaryKey;
    }

    public Class<?> getMember() {
        return member;
    }

    public String getTableName() {
        return tableName;
    }

    public _Field[] getFields() {
        return fields;
    }

    public List<_Field> getForeignKeys(){
        return Arrays.stream(fields)
                .filter(_Field::isFK)
                .collect(Collectors.toList());
    }
    public String getSQL_SELECT() {
        return SQL_SELECT;
    }

    public String getSQL_INSERT() {
        return SQL_INSERT;
    }

    public String getSQL_UPDATE() {
        return SQL_UPDATE;
    }

    public String getSQL_DELETE() {
        return SQL_DELETE;
    }

    private _Field getPrimaryKeyFromSuperClass(Class<?> c) throws Exception {
        var tmp = c.getSuperclass();
        while (!tmp.equals(Object.class)){
            for(var field : tmp.getDeclaredFields()){
                if(field.getAnnotation(PrimaryKey.class)==null) continue;
                var annotations = new __FieldAnnotation(field);
                _Field _field = new _Field(this, field.getName());
                _field.setColumnType(annotations.getColumnType());
                _field.setColumnName(annotations.getColumnName());
                primaryKey = _field;
                _field.setPK(true);
                _field.setFK(false);
                _field.setFieldType(field.getType());
                _field.setNullable(annotations.isNullable());
                var nameCapitalizzed = field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
                _field.setGetter(c.getMethod("get" + nameCapitalizzed));
                _field.setSetter(c.getMethod("set" + nameCapitalizzed,field.getType()));
                return _field;
            }
            //todo check is missing
            tmp = tmp.getSuperclass();
        }
        //todo upgrade exception
        throw new Exception("Class has no Primary Key!");
    }
}

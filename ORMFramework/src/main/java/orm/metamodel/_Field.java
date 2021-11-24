package orm.metamodel;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.lang.Enum.valueOf;


public class _Field {

    private _Entity entity;

    private String name;

    private Class<?> columnType = Void.class;

    private String columnName = "";

    private boolean isPK = false;

    private boolean isFK = false;

    private Class<?> fieldType = Void.class;

    private boolean isNullable = false;

    private Method getter = null;

    private Method setter = null;

    private String assignmentTable = null;

    private String remoteColumnName = null;

    private boolean manyToMany = false;

    private boolean oneToMany = false;

    public _Field(_Entity entity, String name) {
        this.entity = entity;
        this.name = name;
    }

    public _Entity getEntity() {
        return entity;
    }

    public void setEntity(_Entity entity) {
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getColumnType() {
        return columnType;
    }

    public void setColumnType(Class<?> columnType) {
        this.columnType = columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isPK() {
        return isPK;
    }

    public void setPK(boolean PK) {
        isPK = PK;
    }

    public boolean isFK() {
        return isFK;
    }

    public void setFK(boolean FK) {
        isFK = FK;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }

    public Object getValue(Object o) throws InvocationTargetException, IllegalAccessException {
        return fieldType.isEnum() ? getter.invoke(o).toString() : getter.invoke(o);
    }

    public void setValue(Object o, Object value) throws InvocationTargetException, IllegalAccessException {
        if(fieldType.isEnum()){
            Class c = fieldType;
            var enumvalue = getInstance((String)value,c);
            setter.invoke(o, enumvalue);
//            setter.invoke(o, Enum.valueOf( fieldType,(String)value) );
        }else if(fieldType.equals(LocalDate.class)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            String date = value.toString();
            if (date.length() == 10){
                date += " 00:00:00.0";
            }
            try{
                setter.invoke(o,LocalDate.parse(date,formatter));
            }catch (DateTimeParseException e){
                throw new IllegalArgumentException("Cannot parse this value: "+value.getClass()+""+value);
            }

        }else {
            setter.invoke(o, value);
        }

    }

    public String getAssignmentTable() {
        return assignmentTable;
    }

    public void setAssignmentTable(String assignmentTable) {
        this.assignmentTable = assignmentTable;
    }

    public String getRemoteColumnName() {
        return remoteColumnName;
    }

    public void setRemoteColumnName(String remoteColumnName) {
        this.remoteColumnName = remoteColumnName;
    }

    public boolean isManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(boolean manyToMany) {
        this.manyToMany = manyToMany;
    }

    public boolean isOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(boolean oneToMany) {
        this.oneToMany = oneToMany;
    }

    public Method getSetter() {
        return setter;
    }

    public Method getGetter() {
        return getter;
    }
    private static <T extends Enum<T>> Object getInstance(String value,Class<T> t){
        if(t.isEnum()){
            return Enum.valueOf(t,value);
        }
        throw new IllegalArgumentException("wrong argument");
    }
}

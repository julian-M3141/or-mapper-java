package orm.metamodel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        return getter.invoke(o);
    }

    public void setValue(Object o, Object value) throws InvocationTargetException, IllegalAccessException {
        //todo calendar/time/data
        setter.invoke(o,value);
    }
}

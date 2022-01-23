package orm.metamodel;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.lang.Enum.valueOf;

/**
 * This class stores the meta information if a column
 */
public class _Field {

    /**
     * The table of the field
     */
    private _Entity entity;

    /**
     * Stores the name of the field
     */
    private String name;

    /**
     * Stores the type of the column
     */
    private Class<?> columnType = Void.class;

    /**
     * Stores the name of the column
     */
    private String columnName = "";

    /**
     * Is true if the field is the primary key
     */
    private boolean isPK = false;

    /**
     * Is true if the field is the foreign key
     */
    private boolean isFK = false;

    /**
     * Stores the class of the field
     */
    private Class<?> fieldType = Void.class;

    /**
     * Is true if the column is nullable
     */
    private boolean isNullable = false;

    /**
     * Stores the getter of the field
     */
    private Method getter = null;

    /**
     * Stores the setter of the field
     */
    private Method setter = null;

    /**
     * Stores the name of the table where the many-to-many relationship is stored
     */
    private String assignmentTable = null;

    /**
     * Stores the name of the column where the primary key of the current table is stored as foreign key
     */
    private String remoteColumnName = null;

    /**
     * Is true if it is a many-to-many relationship
     */
    private boolean manyToMany = false;

    /**
     * Is true if it is a one-to-many relationship
     */
    private boolean oneToMany = false;

    /**
     * Creates a new _Field object and sets the entity and the name of the field
     * @param entity the entity
     * @param name the name of the field
     */
    public _Field(_Entity entity, String name) {
        this.entity = entity;
        this.name = name;
    }

    // some getters and setters
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

    /**
     * Invokes the underlying getter method
     * @param o the object, on which the method is invoked
     * @return returns the returnvalue of the method
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object getValue(Object o) throws InvocationTargetException, IllegalAccessException {
        return fieldType.isEnum() ? getter.invoke(o).toString() : getter.invoke(o);
    }

    /**
     * Invokes the underlying setter method
     * @param o the object, on which the method is invoked
     * @param value the value which will be set
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void setValue(Object o, Object value) throws InvocationTargetException, IllegalAccessException {
        if(fieldType.isEnum()){
            // stores the enum as a string in a database // todo for later: annotate the enum to decide whether to strore as string or int
            Class c = fieldType;
            var enumvalue = getInstance((String)value,c);
            setter.invoke(o, enumvalue);
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

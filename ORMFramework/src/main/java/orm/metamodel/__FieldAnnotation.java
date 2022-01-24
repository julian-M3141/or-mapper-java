package orm.metamodel;

import orm.annotations.ForeignKey;
import orm.annotations.PrimaryKey;

import java.lang.reflect.Field;

/**
 * A helper class for storing the information of a field
 */
public class __FieldAnnotation {

    /**
     * The name of the field
     */
    private String fieldName = "";

    /**
     * The name of the column in the database
     */
    private String columnName = "";

    /**
     * The type of the column
     */
    private Class<?> columnType = Void.class;

    /**
     * Stores whether the column is nullable
     */
    private boolean isNullable = false;

    /**
     * Stores whether the column is the primary key
     */
    private boolean isPK = false;

    /**
     * Stores whether the field is a foreign key
     */
    private boolean isFK = false;

    /**
     * Stores whether the foreign key is a many to many relationship
     */
    private boolean isManyToMany = false;

    /**
     * Stores whether the foreign key is a one to many relationship
     */
    private boolean isOneToMany = false;

    /**
     * Stores the name of the table where the many-to-many relationship is stored
     */
    private String assignmentTable = "";

    /**
     * Stores the name of the column where the primary key of the current table is stored as foreign key
     */
    private String remoteColumnName = "";

    /**
     * Creates a new Object
     * @param field the Field, from which the information is stored
     */
    public __FieldAnnotation(Field field) {

        //field annotation
        var fann = field.getAnnotation(orm.annotations.Field.class);
        fieldName = field.getName();
        columnName = field.getName();
        columnType = field.getType();
        if(fann != null){
            fieldName = (fann.fieldName() == null || fann.fieldName().isBlank()) ? field.getName() : fann.fieldName();
            columnName = (fann.columnName() == null || fann.columnName().isBlank()) ? field.getName() : fann.columnName();
            columnType = (fann.columnType() == null || fann.columnType().equals(Void.class)) ? field.getType() : fann.columnType();
            if(fann.isNullable()) isNullable = true;
        }

        //primary key annotation
        var pkann = field.getAnnotation(orm.annotations.PrimaryKey.class);
        if(pkann != null){
            isPK = true;
        }

        //foreign key annotation
        var fkann = field.getAnnotation(ForeignKey.class);
        if(fkann != null){
            isFK = true;
            fieldName = (fkann.fieldName() == null || fkann.fieldName().isBlank()) ? field.getName() : fkann.fieldName();
            columnName = (fkann.columnName() == null || fkann.columnName().isBlank()) ? field.getName() : fkann.columnName();
            if(!(fkann.columnType() == null || fkann.columnType().equals(Void.class))) columnType = fkann.columnType();
            if(fkann.isNullAble()) isNullable = true;
            if(fkann.isOneToMany()) {
                isOneToMany = true;
                remoteColumnName = fkann.remoteColumnName();
            }
            else if(fkann.isManyToMany()) {
                isManyToMany = true;
                assignmentTable = fkann.assignmentTable();
                remoteColumnName = fkann.remoteColumnName();
            }
        }
    }

    // some getter for the fields

    public String getFieldName() {
        return fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Class<?> getColumnType() {
        return columnType;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public boolean isPK() {
        return isPK;
    }

    public boolean isFK() {
        return isFK;
    }

    public boolean isManyToMany() {
        return isManyToMany;
    }

    public boolean isOneToMany() {
        return isOneToMany;
    }

    public String getAssignmentTable() {
        return assignmentTable;
    }

    public String getRemoteColumnName() {
        return remoteColumnName;
    }
}

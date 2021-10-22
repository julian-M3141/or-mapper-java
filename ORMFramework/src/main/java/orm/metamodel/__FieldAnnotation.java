package orm.metamodel;

import orm.annotations.ForeignKey;
import orm.annotations.PrimaryKey;

import java.lang.reflect.Field;

public class __FieldAnnotation {
    private String fieldName = "";
    private String columnName = "";
    private Class<?> columnType = Void.class;
    private boolean isNullable = false;
    private boolean isPK = false;
    private boolean isFK = false;

    public __FieldAnnotation(Field field) {

        //field annotation
        //todo set annotation to runtime
        var fann = field.getAnnotation(orm.annotations.Field.class);
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
            /*fieldName = (pkann.fieldName() == null || pkann.fieldName().isBlank()) ? field.getName() : pkann.fieldName();
            columnName = (pkann.columnName() == null || pkann.columnName().isBlank()) ? field.getName() : pkann.columnName();
            columnType = (pkann.columnType() == null || pkann.columnType().equals(Void.class)) ? field.getType() : pkann.columnType();*/
        }

        //foreign key annotation
        var fkann = field.getAnnotation(ForeignKey.class);
        if(fkann != null){
            isFK = true;
            /*fieldName = (fkann.fieldName() == null || fkann.fieldName().isBlank()) ? field.getName() : fkann.fieldName();
            columnName = (fkann.columnName() == null || fkann.columnName().isBlank()) ? field.getName() : fkann.columnName();
            columnType = (fkann.columnType() == null || fkann.columnType().equals(Void.class)) ? field.getType() : fkann.columnType();*/
            if(fann.isNullable()) isNullable = true;
        }
    }

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
}

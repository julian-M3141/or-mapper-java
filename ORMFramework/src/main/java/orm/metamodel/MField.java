package orm.metamodel;

public class MField {

    private MEntity entity;

    private String name;

    private Class<?> columnType = Void.class;

    private String columnName = "";

    private boolean isPK = false;

    private boolean isFK = false;

    private Class<?> fieldType = Void.class;

    private boolean isNullable = false;

    public MField(MEntity entity, String name) {
        this.entity = entity;
        this.name = name;
    }

    public MEntity getEntity() {
        return entity;
    }

    public void setEntity(MEntity entity) {
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
}

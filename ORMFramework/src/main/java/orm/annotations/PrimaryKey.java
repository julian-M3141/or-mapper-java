package orm.annotations;

public @interface PrimaryKey {
    public String fieldName() default "";
    public String columnName() default "";
    public Class<?> columnType() default Void.class;
}

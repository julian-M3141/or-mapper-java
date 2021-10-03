package orm.annotations;

public @interface Field {
    public String fieldName() default "";
    public String columnName() default "";
    public Class columnType() default Void.class;
//    public Class<?> columnType() default Void.class;
    public boolean isNullable() default false;
}

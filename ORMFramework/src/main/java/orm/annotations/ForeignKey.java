package orm.annotations;

public @interface ForeignKey {
    public String fieldName() default "";
    public String columnName() default "";
    public Class<?> columnType() default Void.class;
    public boolean isNullAble() default false;
}

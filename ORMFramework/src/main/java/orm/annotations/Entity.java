package orm.annotations;

public @interface Entity {
    public String tableName() default "";
}

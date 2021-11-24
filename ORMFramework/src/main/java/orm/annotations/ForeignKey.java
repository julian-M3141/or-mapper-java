package orm.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
    public String fieldName() default "";
    public String columnName() default "";
    public Class<?> columnType() default Void.class;
    public boolean isNullAble() default false;
    public String assignmentTable() default "";
    public String remoteColumnName() default "";
    public boolean isManyToMany() default false;
    public boolean isOneToMany() default false;
}

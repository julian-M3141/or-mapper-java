package orm.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
    public String fieldName() default "";
    public String columnName() default "";
    public Class<?> columnType() default Void.class;
    public boolean isNullAble() default false;
}

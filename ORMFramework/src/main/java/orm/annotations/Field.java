package orm.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
    public String fieldName() default "";
    public String columnName() default "";
    public Class columnType() default Void.class;
//    public Class<?> columnType() default Void.class;
    public boolean isNullable() default false;
    public int length() default 255;
}

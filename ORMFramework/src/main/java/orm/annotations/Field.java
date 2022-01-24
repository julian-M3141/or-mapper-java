package orm.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Some configurations for a field and its corresponding column in the table
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
    /**
     * The name of the field.
     * @return
     */
    public String fieldName() default "";

    /**
     * The name of the column in the database
     * @return
     */
    public String columnName() default "";

    /**
     * The type of the column
     * @return
     */
    public Class columnType() default Void.class;

    /**
     * Stores whether the column is nullable.
     * @return
     */
    public boolean isNullable() default false;

    /**
     * The size of the column, default 255
     * @return
     */
    public int length() default 255;
}

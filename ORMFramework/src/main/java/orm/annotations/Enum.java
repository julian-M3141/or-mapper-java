package orm.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This field enables the user to decide the user whether to store the enum as a string or the underlying int
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Enum {
    /**
     * Stores the type how the enum shall be stored
     * @return returns the enum.
     */
    public EnumType enumType() default EnumType.STRING;
}

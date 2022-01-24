package orm.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A field with this annotation will not be stored in the database
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {}

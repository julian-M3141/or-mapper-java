package orm.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The field with this annotation will be the primary key in the database
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
}

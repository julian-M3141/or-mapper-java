package orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A class annotated with Entity will be enabled for ORM functionality.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    /**
     * The name of the table.
     * @return returns the tablename.
     */
    public String tableName() default "";
}

package orm.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Stores information about a foreign key column
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
    /**
     * The name of the field
     * @return
     */
    public String fieldName() default "";

    /**
     * The name of the column
     * @return
     */
    public String columnName() default "";

    /**
     * The type of the column
     * @return
     */
    public Class<?> columnType() default Void.class;

    /**
     * Is true if the field is nullable
     * @return
     */
    public boolean isNullAble() default false;

    /**
     * Stores the name of the table where the many-to-many relationship is stored
     * @return
     */
    public String assignmentTable() default "";

    /**
     * Stores the name of the column where the primary key of this entity is stored as a foreign key
     * @return
     */
    public String remoteColumnName() default "";

    /**
     * is true if it is a many-to-many relationship.
     * @return
     */
    public boolean isManyToMany() default false;

    /**
     * is true if it is a one-to-many relationship.
     * @return
     */
    public boolean isOneToMany() default false;
}

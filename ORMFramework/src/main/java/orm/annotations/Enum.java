package orm.annotations;

public @interface Enum {
    public EnumType enumType() default EnumType.STRING;
}

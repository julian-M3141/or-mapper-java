package demoapp;

import lombok.Data;
import orm.annotations.*;
import orm.annotations.Enum;

import java.time.LocalDate;

@Data
public class Person {

    @PrimaryKey
    @Field(columnName = "id")
    protected long id;

    @Field(columnName = "firstname", length = 50, isNullable = false)
    protected String firstname;

    @Field(columnName = "lastname", length = 50, isNullable = false)
    protected String lastname;

    @Field(columnName = "sex", isNullable = false, length = 50)
    @Enum(enumType = EnumType.STRING)
    protected Sex sex;

    @Ignore
    protected LocalDate birthdate;
}

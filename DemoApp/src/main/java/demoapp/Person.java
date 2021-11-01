package demoapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import orm.annotations.*;
import orm.annotations.Enum;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
<<<<<<< Updated upstream
@Entity(tableName = "persons")
=======
>>>>>>> Stashed changes
public class Person {

    @PrimaryKey
    @Field(columnName = "id")
    protected long id;

    @Field(columnName = "firstname", length = 50, isNullable = false)
    protected String firstname;

    @Field(columnName = "name", length = 50, isNullable = false)
    protected String lastname;

    @Field(columnName = "gender", isNullable = false, length = 50)
    @Enum(enumType = EnumType.STRING)
    protected Sex sex;

    @Ignore
    protected LocalDate birthdate;
}

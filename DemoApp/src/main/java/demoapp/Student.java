package demoapp;


import lombok.Data;
import lombok.NoArgsConstructor;
import orm.annotations.*;
import orm.annotations.Enum;

@NoArgsConstructor
@Data
@Entity(tableName = "Students")
public class Student extends Person {
    @Field(columnName = "grade")
    @Enum(enumType = EnumType.STRING)
    protected Grade grade;

    @Field(columnName = "class", isNullable = false)
    @ForeignKey
    protected MyClass myClass;
}

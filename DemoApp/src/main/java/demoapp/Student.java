package demoapp;


import lombok.Data;
import orm.annotations.*;
import orm.annotations.Enum;

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

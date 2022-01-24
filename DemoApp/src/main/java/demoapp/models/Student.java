package demoapp.models;


import lombok.*;
import orm.annotations.*;
import orm.annotations.Enum;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity(tableName = "students")
public class Student extends Person {
    @Field(columnName = "grade")
    @Enum(enumType = EnumType.STRING)
    protected Grade grade;

    @Field(isNullable = false)
    @ForeignKey(columnName = "fk_class")
    @EqualsAndHashCode.Exclude // as recursive aggregation
    protected MyClass myClass;
}

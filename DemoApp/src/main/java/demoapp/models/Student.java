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

    @Field(columnName = "fk_class", isNullable = false)
    @ForeignKey
    @EqualsAndHashCode.Exclude // as recursive aggregation
    protected MyClass myClass;
}

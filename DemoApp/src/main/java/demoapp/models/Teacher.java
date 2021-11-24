package demoapp.models;

import lombok.*;
import orm.annotations.Entity;
import orm.annotations.Field;
import java.time.LocalDate;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity(tableName = "teachers")
public class Teacher extends Person{
    @Field(columnName = "hdate")
    protected LocalDate hiredate;

    @Field(columnName = "salary")
    protected Integer salary;
}

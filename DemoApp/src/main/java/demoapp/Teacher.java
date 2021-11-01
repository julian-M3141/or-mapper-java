package demoapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import orm.annotations.Entity;
import orm.annotations.Field;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(tableName = "teachers")
public class Teacher extends Person{
    @Field(columnName = "hdate")
    protected LocalDate hiredate;

    @Field(columnName = "salary")
    protected Integer salary;
}

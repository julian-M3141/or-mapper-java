package demoapp;

import orm.annotations.Entity;
import orm.annotations.Field;
import orm.annotations.Ignore;

import java.time.LocalDate;


@Entity(tableName = "teachers")
public class Teacher {
    @Ignore
    protected LocalDate hiredate;

    @Field(columnName = "salary")
    protected Integer salary;
}

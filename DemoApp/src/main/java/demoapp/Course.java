package demoapp;

import orm.annotations.*;
import orm.annotations.Enum;

import java.util.List;


@Entity(tableName = "courses")
public class Course {
    @PrimaryKey
    @Field(columnName = "id")
    protected long id;

    @Field(columnName = "isActive")
    protected boolean active;

    @Field(columnName = "name", length = 50)
    protected String name;

    @ForeignKey
    @Field(columnName = "fk_teacher")
    protected Teacher teacher;


    @Ignore
    protected List<Student> students;
}

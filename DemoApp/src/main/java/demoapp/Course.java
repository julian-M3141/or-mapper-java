package demoapp;

import lombok.Data;
import lombok.NoArgsConstructor;
import orm.annotations.*;
import orm.annotations.Enum;

import java.util.List;


@Data
@NoArgsConstructor
@Entity(tableName = "courses")
public class Course {
    @PrimaryKey
    @Field(columnName = "id")
    protected String id;

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

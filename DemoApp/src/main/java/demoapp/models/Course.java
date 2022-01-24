package demoapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import orm.annotations.*;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(tableName = "courses")
public class Course {
    @PrimaryKey
    @Field(columnName = "id")
    protected String id;

    @Field(columnName = "isActive")
    protected boolean active;

    @Field(columnName = "name", length = 50)
    protected String name;

    @ForeignKey(columnName = "fk_teacher")
    protected Teacher teacher;


    @ForeignKey(assignmentTable = "student_courses",
            columnName = "fk_course",
            remoteColumnName = "fk_student",
            isManyToMany = true,
            columnType = Student.class)
    protected List<Student> students = new ArrayList<>();
}

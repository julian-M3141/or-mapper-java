package demoapp.models;


import lombok.*;
import orm.annotations.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(tableName = "classes")
public class MyClass {

    @PrimaryKey
    @Field(columnName = "id")
    protected String id;

    @Field(columnName = "name", length = 50)
    protected String name;

    @ForeignKey
    @Field(columnName = "fk_teacher")
    protected Teacher teacher;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude  // without, equal would be infinitely circular
    @ForeignKey(isOneToMany = true, remoteColumnName = "fk_class", columnType = Student.class)
    @Field
    List<Student> students;
}

package demoapp;


import lombok.Data;
import orm.annotations.Field;
import orm.annotations.ForeignKey;
import orm.annotations.PrimaryKey;

@Data
public class MyClass {

    @PrimaryKey
    @Field(columnName = "id")
    protected long id;

    @Field(columnName = "name", length = 50)
    protected String name;

    @ForeignKey
    @Field(columnName = "fk_teacher")
    protected Teacher teacher;
}

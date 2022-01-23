package demoapp.samples;

import demoapp.models.Student;
import orm.ORM;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class ShowDelete {
    public static void show(){
        var person = ORM.get(Student.class,"st1");

        try {
            ORM.delete(person);
        } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

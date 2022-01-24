package demoapp.samples;

import demoapp.models.Student;
import orm.ORM;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class ShowDelete {
    public static void show(){
        System.out.println("\n\n[7.1] Show delete");
        System.out.println("######################################\n");

        var person = ORM.get(Student.class,"st1");


        try {
            ORM.delete(person);
        } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

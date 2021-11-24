package demoapp.samples;

import demoapp.models.*;
import orm.ORM;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ShowSelect {
    public static void show(){
        System.out.println("[2] Demonstrate select");
        System.out.println("######################################\n");

        //show simple save
        System.out.println("[2.1] Show simple select");
        System.out.println("######################################\n");
        var person = ORM.get(Person.class,"1234");
        System.out.println(person);


//         show save with inheritance
        System.out.println("\n\n[2.2] Show select with inheritance (table per type)");
        System.out.println("######################################\n");
        var teacher = ORM.get(Teacher.class, "123L");
        System.out.println(teacher);


        System.out.println("\n\n[2.3] Show select with fk (one to many)");
        System.out.println("######################################\n");
        //show one to many
        var myClass = ORM.get(MyClass.class,"c.1");

        System.out.println(myClass);
//
//
//
//
//        //show many to many
//
//        System.out.println("\n\n[1.5] Show save with fk (many to many)");
//        System.out.println("######################################\n");
//        course.setStudents(List.of(student1,student2));
//
//        try {
//            ORM.save(course);
//        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
//            e.printStackTrace();
//        }
    }
}

package demoapp.samples;

import demoapp.models.*;
import orm.ORM;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.zip.CheckedOutputStream;

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


        System.out.println("\n\n[2.3] Show select with fk (one to many and many to one) ");
        System.out.println("######################################\n");
        //show one to many
        var myClass = ORM.get(MyClass.class,"c.1");

        System.out.println(myClass);

        System.out.println(myClass.getStudents());


        //show many to many

        System.out.println("\n\n[2.4] Show save with fk (many to many)");
        System.out.println("######################################\n");

        var course = ORM.get(Course.class, "12");

        System.out.println(course);
    }
}

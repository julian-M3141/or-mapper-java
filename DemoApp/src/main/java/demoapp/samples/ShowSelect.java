package demoapp.samples;

import demoapp.models.*;
import orm.Cache;
import orm.ORM;

public class ShowSelect {
    public static void show(){
        ORM.setCache(new Cache());
        System.out.println();
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


        //empty cache
        ORM.setCache(new Cache());

        System.out.println("\n\n[2.3] Show select with fk (one to many and many to one) ");
        System.out.println("######################################\n");
        //show one to many
        var myClass = ORM.get(MyClass.class,"c.1");

        System.out.println(myClass);

        System.out.println(myClass.getStudents());


        //show many to many

        System.out.println("\n\n[2.4] Show select with fk (many to many)");
        System.out.println("######################################\n");

        ORM.setCache(new Cache());
        var course = ORM.get(Course.class, "12");

        System.out.println(course);
    }
}

package demoapp.samples;

import demoapp.models.*;
import orm.Cache;
import orm.ORM;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;

public class ShowUpdate {
    public static void show(){
        System.out.println();
        System.out.println("[3] Demonstrate update");
        System.out.println("######################################\n");

        //show simple save
        System.out.println("[3.1] Show simple update");
        System.out.println("######################################\n");
        var person = ORM.get(Person.class,"1234");
        System.out.println(person);
        person.setFirstname("neuer Vorname");
        try {
            ORM.save(person);
        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println(ORM.get(Person.class,"1234"));


//         show save with inheritance
        System.out.println("\n\n[3.2] Show update with inheritance (table per type)");
        System.out.println("######################################\n");
        var teacher = ORM.get(Teacher.class, "123L");
        System.out.println(teacher);
        teacher.setSalary(10000);
        teacher.setLastname("Musterlehrer");
        try {
            ORM.save(teacher);
        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println(ORM.get(Teacher.class,"123L"));


        System.out.println("\n\n[3.3] Show update with fk (one to many and many to one) ");
        System.out.println("######################################\n");
        //show one to many
        var myClass = ORM.get(MyClass.class,"c.1");
        System.out.println(myClass);
        System.out.println(myClass.getStudents());

        var teacher2 = new Teacher();
        teacher2.setId("l.1");
        teacher2.setFirstname("Maxima");
        teacher2.setLastname("Musterlehrerin");
        teacher2.setBirthdate(LocalDate.of(1970,1,1));
        teacher2.setSalary(2234);
        teacher2.setSex(Sex.FEMALE);
        teacher2.setHiredate(LocalDate.now());
        //many to one
        myClass.setTeacher(teacher2);
        var student = new Student();
        student.setId("st3");
        student.setFirstname("Anton");
        student.setLastname("Student");
        student.setSex(Sex.MALE);
        student.setBirthdate(LocalDate.of(1992,1,1));
        student.setGrade(Grade.C);
        student.setMyClass(myClass);
        myClass.getStudents().add(student);
        try {
            ORM.save(myClass);
        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        ORM.setCache(new Cache());
        var newClass = ORM.get(MyClass.class, "c.1");
        System.out.println(newClass);
        System.out.println(newClass.getStudents());

        //show many to many

        System.out.println("\n\n[2.4] Show save with fk (many to many)");
        System.out.println("######################################\n");

        var course = ORM.get(Course.class, "12");
        var student3 = new Student();
        student3.setId("st3");
        student3.setFirstname("Max");
        student3.setLastname("Musterstudent1");
        student3.setSex(Sex.MALE);
        student3.setBirthdate(LocalDate.of(1996,1,1));
        student3.setGrade(Grade.A);
        student3.setMyClass(myClass);
        course.getStudents().add(student3);

        try {
            ORM.save(course);
        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        System.out.println(course);
    }
}

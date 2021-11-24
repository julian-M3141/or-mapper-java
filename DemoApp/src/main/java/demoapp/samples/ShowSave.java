package demoapp.samples;

import demoapp.models.*;
import orm.ORM;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ShowSave {
    public static void show(){


        System.out.println("[1] Demonstrate save");
        System.out.println("######################################\n");

        //show simple save
        System.out.println("[1.1] Show simple save");
        System.out.println("######################################\n");
        var person = new Person("1234","max1","mustermann1",Sex.MALE,LocalDate.of(1970,1,2));
        try {
            ORM.save(person);
        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        // show save with inheritance
        System.out.println("\n\n[1.2] Show save with inheritance (table per type)");
        System.out.println("######################################\n");
        var teacher = new Teacher();
        teacher.setId("123L");
        teacher.setFirstname("Max");
        teacher.setLastname("Musterlehrer1");
        teacher.setBirthdate(LocalDate.of(1970,1,1));
        teacher.setSalary(1234);
        teacher.setSex(Sex.MALE);
        teacher.setHiredate(LocalDate.now());

        try {
            ORM.save(teacher);
        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        // show simple fk (many to one)
        System.out.println("\n\n[1.3] Show save with fk (many to one)");
        System.out.println("######################################\n");
        var course = new Course();
        course.setId("12");
        course.setActive(true);
        course.setName("course");
        course.setTeacher(teacher);

        try {
            ORM.save(course);
        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        System.out.println("\n\n[1.4] Show save with fk (one to many)");
        System.out.println("######################################\n");
        //show one to many
        var myClass = new MyClass("c.1","class 1",teacher, null);

        var student1 = new Student();
        student1.setId("st1");
        student1.setFirstname("Max");
        student1.setLastname("Musterstudent");
        student1.setSex(Sex.MALE);
        student1.setBirthdate(LocalDate.of(1996,1,1));
        student1.setGrade(Grade.A);
        student1.setMyClass(myClass);

        var student2 = new Student();
        student2.setId("st2");
        student2.setFirstname("Maxima");
        student2.setLastname("Musterstudentin");
        student2.setSex(Sex.FEMALE);
        student2.setBirthdate(LocalDate.of(1995,1,1));
        student2.setGrade(Grade.B);
        student2.setMyClass(myClass);

        myClass.setStudents(List.of(student1,student2));

        try {
            ORM.save(myClass);
        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }




        //show many to many

        System.out.println("\n\n[1.5] Show save with fk (many to many)");
        System.out.println("######################################\n");
        course.setStudents(List.of(student1,student2));

        try {
            ORM.save(course);
        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }
}

package demoapp;

import orm.ORM;

import java.lang.reflect.InvocationTargetException;
<<<<<<< Updated upstream
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
=======
import java.sql.SQLException;
>>>>>>> Stashed changes
import java.time.LocalDate;

public class Main {

    
    public static void main(String[] args){
<<<<<<< Updated upstream
//        var teacher = new Teacher();
//        teacher.setId(123L);
//        teacher.setFirstname("Max");
//        teacher.setLastname("Musterlehrer");
//        teacher.setBirthdate(LocalDate.of(1970,1,1));
//        teacher.setSalary(1234);
//        teacher.setSex(Sex.MALE);
//
//
//        try {
//            ORM.save(teacher);
//        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
//            e.printStackTrace();
//        }

//        var person = new Person(123L,"max","mustermann",Sex.MALE,LocalDate.now());
//        var teacher =  new Teacher(LocalDate.now(),1234);
//        System.out.println(ORM.getEntity(person).getSQL_SELECT());
//        System.out.println(ORM.getEntity(person).getSQL_UPDATE());
//        System.out.println(ORM.getEntity(person).getSQL_INSERT());
//        System.out.println(ORM.getEntity(person).getSQL_DELETE());
//
//        System.out.println(ORM.getEntity(teacher).getSQL_SELECT());
//        System.out.println(ORM.getEntity(teacher).getSQL_UPDATE());
//        System.out.println(ORM.getEntity(teacher).getSQL_INSERT());
//        System.out.println(ORM.getEntity(teacher).getSQL_DELETE());
=======
        var teacher = new Teacher();
        teacher.setId(123L);
        teacher.setFirstname("Max");
        teacher.setLastname("Musterlehrer");
        teacher.setBirthdate(LocalDate.of(1970,1,1));
        teacher.setSalary(1234);
        teacher.setSex(Sex.MALE);


        try {
            ORM.save(teacher);
        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
>>>>>>> Stashed changes
    }
}

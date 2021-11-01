package demoapp;

import orm.ORM;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {

    
    public static void main(String[] args){
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


//        worked
//        var person = new Person("1234","max1","mustermann1",Sex.MALE,LocalDate.of(1970,1,2));
//        try {
//            ORM.save(person);
//        } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
//            e.printStackTrace();
//        }

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
//
//        System.out.println(ORM.getEntity(((Person) teacher)).getSQL_INSERT());



//        var person = new Person(123L,"max","mustermann",Sex.MALE,LocalDate.now());
//        try {
//            var conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
//                    "testuser", "testpwd");
//
//            String sql = "INSERT INTO persons values (?,?,?,?,?)"+
//                    " ON CONFLICT (id)"+
//                    " DO UPDATE SET name = ? where persons.id = ?;";
//
//            var stmt = conn.prepareStatement(sql);
//
//            stmt.setObject(1,"123");
//            stmt.setObject(2,"nachname");
//            stmt.setObject(3,"vorname");
//            stmt.setObject(4,"MALE");
//            stmt.setDate(5,Date.valueOf(LocalDate.now()));
//            stmt.setObject(6,"neuerneuernachname");
//            stmt.setObject(7,"123");
//
//            stmt.execute();
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }


    }
}

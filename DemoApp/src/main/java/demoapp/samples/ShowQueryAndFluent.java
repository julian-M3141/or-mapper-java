package demoapp.samples;

import demoapp.models.Person;
import demoapp.models.Student;
import orm.Cache;
import orm.ORM;

import java.sql.SQLException;

public class ShowQueryAndFluent {
    public static void showQuery(){
        try {
            System.out.println("\n\n[5.1] Show custom select with single record");
            System.out.println("######################################\n");
            var person = ORM.query("SELECT * from students WHERE id = 'st1'")
                    .executeQueryOne(Student.class);
            System.out.println(person);


            System.out.println("\n\n[5.2] Show custom select with multiple records");
            System.out.println("######################################\n");
            var persons = ORM.query("SELECT * from students")
                    .executeQueryMany(Student.class);
            System.out.println(persons);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showFluent(){
        try {

            System.out.println("\n\n[6.1] Show fluent api 1");
            System.out.println("######################################\n");
            // get student with id st1
            // the tablename in front of the column is required, as id is a column in students and persons table
            var student = ORM.get(Student.class).equalTo("students.id","st1")
                    .executeQueryOne();
            System.out.println(student);

            System.out.println("\n\n[6.2] Show fluent api 2");
            System.out.println("######################################\n");
            // get students where id = st1 or id = st2
            var student2 = ORM.get(Student.class).equalTo("students.id","st1").or().equalTo("students.id","st2")
                    .executeQueryMany();
            System.out.println(student2);

            System.out.println("\n\n[6.3] Show fluent api 3");
            System.out.println("######################################\n");
            // get the persons with grade greater than A or Firstname starting with Ma
            ORM.setCache(new Cache());
            var persons = ORM.get(Student.class)
                    .greaterThan("grade",'A')
                    .or().like("firstname","Ma%")
                    .executeQueryMany();
            System.out.println(persons);


            System.out.println("\n\n[6.4] Show count");
            System.out.println("######################################\n");
            //get how many persons are stored in the persons database
            System.out.println(ORM.get(Person.class).count());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

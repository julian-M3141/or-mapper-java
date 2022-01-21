package demoapp.samples;

import demoapp.models.Person;
import demoapp.models.Student;
import orm.ORM;

import java.sql.SQLException;

public class ShowQueryAndFluent {
    public static void showQuery(){
        try {
            var person = ORM.query("SELECT * from students WHERE id = 'st1'")
                    .executeQueryOne(Student.class);
            System.out.println(person);

            var persons = ORM.query("SELECT * from students")
                    .executeQueryMany(Student.class);
            System.out.println(persons);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showFluent(){
        try {
            var student = ORM.get(Student.class).equalTo("students.id","st1")
                    .executeQueryOne();
            System.out.println(student);

            var student2 = ORM.get(Student.class).equalTo("students.id","st1").or().equalTo("students.id","st2")
                    .executeQueryMany();
            System.out.println(student2);

            var persons = ORM.get(Student.class)
                    .greaterThan("grade",'A')
                    .or().like("firstname","Ma%")
                    .executeQueryMany();

            System.out.println(persons);

            System.out.println(ORM.get(Person.class).count());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

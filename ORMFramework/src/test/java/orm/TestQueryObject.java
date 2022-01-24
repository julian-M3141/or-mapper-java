package orm;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import orm.annotations.Entity;
import orm.annotations.PrimaryKey;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestQueryObject {

    @Data
    @AllArgsConstructor
    @Entity
    public static class Person {
        @PrimaryKey
        protected Long id;
        protected String firstname;
        protected String lastname;
    }

    @Test
    public void testSimpleFluent(){
        var qo = new QueryObject(Person.class,"SELECT * FROM Person").greaterThan("id",5).and().smallerThan("id",20);
        assertEquals(qo.getSQL(), "SELECT * FROM Person WHERE id > 5 AND id < 20;");
    }

    @Test
    public void testSimpleFluent2(){
        var qo = new QueryObject(Person.class,"SELECT * FROM Person")
                .like("firstname","Ma%")
                .or().in("nachname", List.of("Mustermann","Musterfrau"));
        assertEquals(qo.getSQL(), "SELECT * FROM Person WHERE firstname LIKE 'Ma%' OR nachname IN ('Mustermann', 'Musterfrau');");
    }

    @Test
    public void testNot(){
        var qo = new QueryObject(Person.class,"SELECT * FROM Person").not().equalTo("id",1);
        assertEquals(qo.getSQL(), "SELECT * FROM Person WHERE NOT id = 1;");
    }

    @Test
    public void testNotInverted(){
        var qo = new QueryObject(Person.class,"SELECT * FROM Person").not().not().equalTo("id",1);
        assertEquals(qo.getSQL(), "SELECT * FROM Person WHERE id = 1;");
    }
}

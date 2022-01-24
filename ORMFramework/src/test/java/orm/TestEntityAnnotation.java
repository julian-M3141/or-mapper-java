package orm;

import lombok.Data;
import org.junit.jupiter.api.Test;
import orm.annotations.Entity;
import orm.annotations.Field;
import orm.annotations.PrimaryKey;
import orm.metamodel._Entity;

import static org.junit.jupiter.api.Assertions.*;

public class TestEntityAnnotation {
    @Entity(tableName = "different")
    @Data
    private static class Test1{
        @PrimaryKey
        String id;
    }

    @Entity
    @Data
    private static class Test2{
        @PrimaryKey
        @Field(columnName = "otherid")
        String id;
    }

    @Test
    public void testEntity(){
        _Entity entity = null;
        try {
            entity = new _Entity(Test1.class);
            assertEquals(entity.getTableName(),"different");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testEntityWithDefaultTableName(){
        _Entity entity = null;
        try {
            entity = new _Entity(Test2.class);
            assertEquals(entity.getTableName(),"Test2");
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testFieldNameDefault(){
        _Entity entity = null;
        try {
            entity = new _Entity(Test1.class);
            assertEquals(entity.getPrimaryKey().getColumnName(),"id");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testColumnNameWithDifferentName(){
        _Entity entity = null;
        try {
            entity = new _Entity(Test2.class);
            assertEquals(entity.getPrimaryKey().getColumnName(),"otherid");
        } catch (Exception e) {
            fail();
        }

    }


    @Test
    public void testSQLinsert(){
        _Entity entity = null;
        try {
            entity = new _Entity(Test2.class);
            assertEquals(entity.getSQL_INSERT(),"INSERT INTO Test2 (otherid) VALUES (?)");
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testSQLselect(){
        _Entity entity = null;
        try {
            entity = new _Entity(Test2.class);
            assertEquals(entity.getSQL_SELECT(),"SELECT otherid FROM Test2 WHERE otherid=?;");
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testSQLdelete(){
        _Entity entity = null;
        try {
            entity = new _Entity(Test2.class);
            assertEquals(entity.getSQL_DELETE(),"DELETE FROM Test2 WHERE otherid=?;");
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testSQLupdate(){
        _Entity entity = null;
        try {
            entity = new _Entity(Test2.class);
            assertEquals(entity.getSQL_UPDATE(),"ON CONFLICT (otherid) DO UPDATE SET  WHERE Test2.otherid=?;");
        } catch (Exception e) {
            fail();
        }

    }
}

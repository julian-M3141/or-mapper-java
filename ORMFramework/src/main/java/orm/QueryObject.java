package orm;

import java.sql.SQLException;
import java.util.List;

public class QueryObject {
    private StringBuilder command = new StringBuilder();
    public QueryObject(String command){
        addSQL(command);
    }

    public void addSQL(String command){
        this.command.append(command);
    }

    public String getSQL(){
        return command.toString();
    }

    public QueryObject where(String columname){
        return this;
    }

    public QueryObject greaterThan(Object value){
        return this;
    }

    public QueryObject greaterEqualThan(Object value){
        return this;
    }

    public QueryObject smallerThan(Object value){
        return this;
    }

    public QueryObject smallerEqualThan(Object value){
        return this;
    }

    public QueryObject equalTo(Object value){
        return this;
    }

    public QueryObject groupBy(String columname){
        return this;
    }

    public QueryObject orderBy(Object value, ORDER order){
        return this;
    }

    public QueryObject and(){
        return this;
    }

    public QueryObject or(Object value){
        return this;
    }
    public QueryObject not(Object value){
        return this;
    }

    public QueryObject like(String value){
        return this;
    }

    public QueryObject in(List<Object> values){
        return this;
    }

    public QueryObject between(Object value1, Object value2){
        return this;
    }




    public <T> T executeQueryOne(Class<T> c) throws SQLException{
        return ORM.executeQueryOne(c,this);
    }
    public <T> List<T> executeQueryMany(Class<T> c) throws SQLException{
        return ORM.executeQueryMany(c,this);
    }
}

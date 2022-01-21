package orm;

import java.sql.SQLException;
import java.util.List;

public class QueryObject {
    private StringBuilder command = new StringBuilder();
    private Class<?> c = null;

    private boolean condition = false;
    private boolean nextCondition = true;

    public QueryObject(String command){
        addSQL(command);
    }
    public QueryObject(Class<?> t, String command){
        this.c = t;
        addSQL(command);
    }

    public void addSQL(String command){
        this.command.append(command);
    }



    public String getSQL(){
        return command+";";
    }

    private void addCondition(String sql){
        if(!condition){
            addSQL(" WHERE");
            condition = true;
        }
        if(!nextCondition){
            addSQL(" NOT");
            nextCondition = true;
        }
        addSQL(sql);
    }

    private Object setStringParenthisis(Object o){
        if(o.getClass().equals(String.class) || o.getClass().equals(Character.class)){
            return "'" + o + "'";
        }
        return o;
    }

    public QueryObject greaterThan(String columnname, Object value){
        value = setStringParenthisis(value);
        addCondition(" " + columnname + " > "+value);
        return this;
    }

    public QueryObject greaterEqualThan(String columnname, Object value){
        value = setStringParenthisis(value);
        addCondition(" " + columnname + " >= "+value);
        return this;
    }

    public QueryObject smallerThan(String columnname, Object value){
        value = setStringParenthisis(value);
        addCondition(" " + columnname + " < "+value);
        return this;
    }

    public QueryObject smallerEqualThan(String columnname, Object value){
        value = setStringParenthisis(value);
        addCondition(" " + columnname + " <= "+value);
        return this;
    }

    public QueryObject equalTo(String columnname, Object value){
        value = setStringParenthisis(value);
        addCondition(" " + columnname + " = "+value);
        return this;
    }

    //maybe for later
    /*public QueryObject groupBy(String columname){
        return this;
    }*/


    public QueryObject orderBy(ORDER order, String... columnnames){
        addSQL(" ORDER BY "+ String.join(", ", columnnames) + " "+order.toString());
        return this;
    }

    public QueryObject and(){
        addSQL(" AND");
        return this;
    }

    public QueryObject or(){
        addSQL(" OR");
        return this;
    }
    public QueryObject not(){
        nextCondition = !nextCondition;
        return this;
    }

    public QueryObject like(String columname, String pattern){
        addCondition(" "+columname+" LIKE '"+pattern+"'");
        return this;
    }

    public QueryObject in(String columnname, List<String> values){
        addCondition(" "+columnname+ " IN ('"+ String.join("', '", values) +"')");
        return this;
    }

    public QueryObject between(String columnname, Object value1, Object value2){
        value1 = setStringParenthisis(value1);
        value2 = setStringParenthisis(value2);
        addCondition(" "+columnname+" BETWEEN "+value1+" AND "+value2);
        return this;
    }



    //todo better query for inheritance

    public <T> T executeQueryOne(Class<T> c) throws SQLException{
        return ORM.executeQueryOne(c,this);
    }
    public <T> List<T> executeQueryMany(Class<T> c) throws SQLException{
        return ORM.executeQueryMany(c,this);
    }

    public <T> void execute(Class<T> c) throws SQLException {
        ORM.execute(c,this);
    }

    public Object executeQueryOne() throws SQLException{
        if(c == null){
            throw new NullPointerException("Class not defined");
        }
        return ORM.executeQueryOne(c,this);
    }
    public List<?> executeQueryMany() throws SQLException{
        if(c == null){
            throw new NullPointerException("Class not defined");
        }
        return ORM.executeQueryMany(c,this);
    }

    public void execute() throws SQLException {
        if(c == null){
            throw new NullPointerException("Class not defined");
        }
        ORM.execute(c,this);
    }

    public int count() throws SQLException {
        if(c == null){
            throw new NullPointerException("Class not defined");
        }
        command = new StringBuilder("SELECT count(*) FROM "+ORM.getEntity(c).getTableName());
        return ORM.count(this);
    }
}

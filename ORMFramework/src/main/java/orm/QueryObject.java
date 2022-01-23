package orm;

import java.sql.SQLException;
import java.util.List;

/**
 * This class provides functionality for creating a sql command.
 * @author julian
 */
public class QueryObject {

    /**
     * This fields contains the sql command.
     */
    private StringBuilder command = new StringBuilder();

    /**
     * This field holds the class, which the query will return.
     */
    private Class<?> c = null;

    /**
     * True if a condition was added to the query and false otherwise
     */
    private boolean condition = false;

    /**
     * Holds whether the next condition is inverted
     */
    private boolean nextCondition = true;

    /**
     * Creates a new QueryObject.
     * @param command the start of the sql command.
     */
    public QueryObject(String command){
        addSQL(command);
    }

    /**
     * Creates a new QueryObject.
     * @param t the class of the returned object of the query.
     * @param command the start of the sql command.
     */
    public QueryObject(Class<?> t, String command){
        this.c = t;
        addSQL(command);
    }

    /**
     * Adds a new part to the sql query.
     * @param command the sql command.
     */
    public void addSQL(String command){
        this.command.append(command);
    }

    /**
     * Returns the sql command.
     * @return returns the sql command.
     */
    public String getSQL(){
        return command+";";
    }

    /**
     * adds a new condition to the query.
     * @param sql the condition.
     */
    private void addCondition(String sql){
        // if its the first condition put a where in front of it
        if(!condition){
            addSQL(" WHERE");
            condition = true;
        }
        // if the condition is inverted, put a NOT inifront of the condition
        if(!nextCondition){
            addSQL(" NOT");
            nextCondition = true;
        }
        addSQL(sql);
    }

    /**
     * Adds quotes if the object is a String or Character.
     * @param o the object wich will be checked.
     * @return the object with quotes if the object is a string or char.
     */
    private Object setStringQuotes(Object o){
        if(o.getClass().equals(String.class) || o.getClass().equals(Character.class)){
            return "'" + o + "'";
        }
        return o;
    }

    /**
     * Adds a condition to the query, where the value of the column is greater than the provided value.
     * @param columnname the columnname on the left side of the condition.
     * @param value the value on the right side of the condition.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject greaterThan(String columnname, Object value){
        value = setStringQuotes(value);
        addCondition(" " + columnname + " > "+value);
        return this;
    }

    /**
     * Adds a condition to the query, where the value of the column is greater equal than the provided value.
     * @param columnname the columnname on the left side of the condition.
     * @param value the value on the right side of the condition.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject greaterEqualThan(String columnname, Object value){
        value = setStringQuotes(value);
        addCondition(" " + columnname + " >= "+value);
        return this;
    }

    /**
     * Adds a condition to the query, where the value of the column is smaller than the provided value.
     * @param columnname the columnname on the left side of the condition.
     * @param value the value on the right side of the condition.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject smallerThan(String columnname, Object value){
        value = setStringQuotes(value);
        addCondition(" " + columnname + " < "+value);
        return this;
    }

    /**
     * Adds a condition to the query, where the value of the column is smaller equal than the provided value.
     * @param columnname the columnname on the left side of the condition.
     * @param value the value on the right side of the condition.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject smallerEqualThan(String columnname, Object value){
        value = setStringQuotes(value);
        addCondition(" " + columnname + " <= "+value);
        return this;
    }

    /**
     * Adds a condition to the query, where the value of the column is equal to the provided value.
     * @param columnname the columnname on the left side of the condition.
     * @param value the value on the right side of the condition.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject equalTo(String columnname, Object value){
        value = setStringQuotes(value);
        addCondition(" " + columnname + " = "+value);
        return this;
    }

    //maybe for later
    /*public QueryObject groupBy(String columname){
        return this;
    }*/

    /**
     * Adds an order clause to the query.
     * @param order the order of the query (ASC or DESC)
     * @param columnnames the columnnames, which will be effected by the order.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject orderBy(ORDER order, String... columnnames){
        addSQL(" ORDER BY "+ String.join(", ", columnnames) + " "+order.toString());
        return this;
    }

    /**
     * Connects the next condition with an and.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject and(){
        addSQL(" AND");
        return this;
    }

    /**
     * Connects the next condition with an or.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject or(){
        addSQL(" OR");
        return this;
    }

    /**
     * Inverts the next condition.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject not(){
        nextCondition = !nextCondition;
        return this;
    }

    /**
     * Adds a condition to the query, column should be like the provided pattern.
     * @param columnname the columnname on the left side of the condition.
     * @param pattern the pattern.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject like(String columnname, String pattern){
        addCondition(" "+columnname+" LIKE '"+pattern+"'");
        return this;
    }

    /**
     * Adds a condition to the query, column value should be equal to one of the values.
     * @param columnname the columnname on the left side of the condition.
     * @param values the values that are looked for.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject in(String columnname, List<String> values){
        addCondition(" "+columnname+ " IN ('"+ String.join("', '", values) +"')");
        return this;
    }

    /**
     * Adds a condition to the query, column value should be the two values.
     * @param columnname the columnname on the left side of the condition.
     * @param value1 the minimum value.
     * @param value2 the maximum value.
     * @return returns a reference to itself to build a query.
     */
    public QueryObject between(String columnname, Object value1, Object value2){
        value1 = setStringQuotes(value1);
        value2 = setStringQuotes(value2);
        addCondition(" "+columnname+" BETWEEN "+value1+" AND "+value2);
        return this;
    }

    /**
     * Executes the query and returns the requested object.
     * @param c the class of the returned object.
     * @param <T> the generic type of the returned object.
     * @return the requested object.
     * @throws SQLException
     */
    public <T> T executeQueryOne(Class<T> c) throws SQLException{
        return ORM.executeQueryOne(c,this);
    }

    /**
     * Executes the query and returns a list of the requested objects.
     * @param c the class of the returned list.
     * @param <T> the generic type of the returned list.
     * @return the requested list of objects.
     * @throws SQLException
     */
    public <T> List<T> executeQueryMany(Class<T> c) throws SQLException{
        return ORM.executeQueryMany(c,this);
    }

    /**
     * Executes a query without a return value.
     * @param c the class on which the query is about.
     * @param <T> the generic type of the class
     * @throws SQLException
     */
    public <T> void execute(Class<T> c) throws SQLException {
        ORM.execute(c,this);
    }

    /**
     * Executes the query and returns the requested object.
     * @return the requested object.
     * @throws SQLException
     */
    public Object executeQueryOne() throws SQLException{
        if(c == null){
            throw new NullPointerException("Class not defined");
        }
        return ORM.executeQueryOne(c,this);
    }

    /**
     * Executes the query and returns a list of the requested objects.
     * @return the requested list of objects.
     * @throws SQLException
     */
    public List<?> executeQueryMany() throws SQLException{
        if(c == null){
            throw new NullPointerException("Class not defined");
        }
        return ORM.executeQueryMany(c,this);
    }

    /**
     * Executes a query without a return value.
     * @throws SQLException
     */
    public void execute() throws SQLException {
        if(c == null){
            throw new NullPointerException("Class not defined");
        }
        ORM.execute(c,this);
    }

    /**
     * Returns the number of records of a table
     * @return the number of records
     * @throws SQLException
     */
    public int count() throws SQLException {
        if(c == null){
            throw new NullPointerException("Class not defined");
        }
        command = new StringBuilder("SELECT count(*) FROM "+ORM.getEntity(c).getTableName());
        return ORM.count(this);
    }
}

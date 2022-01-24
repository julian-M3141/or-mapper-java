# or-mapper-java

This framework supports functionality for code-first or-mapping using a PostgreSQL database. It supports saving, updating, selecting and deleting objects.
It also supports one-to-many and many-to-many relationships, custom queries, a fluent api for custom conditions and ordering.
Inheritance is implemented with table-per-type, 

## usage

### configuration file

The configuration file *orm.properties* in the *resources*-folder stores information about url, user and password.
It also determines whether the sql commands should be logged, default is true. 

```
show-sql=true
dburl=jdbc:postgresql://localhost:5432/testdb
dbuser=testuser
dbpassword=testpwd
```


### Configuring a class


```
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity(tableName = "persons")
    public class Person {

        @PrimaryKey
        @Field(columnName = "id")
        protected Long id;

        @Field(columnName = "name", length = 50, isNullable = false)
        protected String name;
    
        @Field(columnName = "age", length = 50, isNullable = false)
        protected int age;
    }
```

Lombok annotation *@Data* is used for getter, setter, toString, hashCode and equal methods
and *@NoArgsConstructor* and *@AllArgsConstructor* for creating constructors.

The class has to be annoted with *@Entity* and enables to specify the tablename, default name is the class name.

The primary key is annotated with *@PrimaryKey*, a field is annotated with *@Field* and enables to specify the columnname, the length and whether the field is nullable.

For more details and possibilities see the full documentation in the javadoc folder.

### save

The static *save* method saves or updates an object in the database.

`ORM.save(person)`

### select

The static *get* method selects an object in the database.

`ORM.get(Perosn.class, 1)`

### delete

The static *delete* method deletes the required object in the database.

`ORM.delete(person)`

### custom sql query

`ORM.query("select * from persons")`

### fluent api

The fluent api provides functionality for custom conditions and ordering.

`ORM.get(Person.class).equalTo("id",1).or().greaterThan("age",18)`

For getting the number of records in a table, use

`ORM.get(Person.class).count()`

For full functionality see the documentation

### Execute a custom or fluent query

If only one record is expected to be returned, use

`ORM.query("select * from persons where id='1'").executeQueryOne(Person.class)`

If more than one record is expected, use

`ORM.query("select * from persons").executeQueryMany(Person.class)`

which will return a list of the requested objects

For custom update, delete or insert statements, use the execute command

`ORM.query("delete from persons where id=1").execute()`

### things that are not implemented

* lazy loading
* locking
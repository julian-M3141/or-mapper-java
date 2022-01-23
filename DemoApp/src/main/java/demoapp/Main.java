package demoapp;

import demoapp.models.Person;
import demoapp.models.Student;
import demoapp.samples.*;
import orm.Cache;
import orm.ORDER;
import orm.ORM;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
    
    public static void main(String[] args){

        //start database with docker compose (db folder) (only works with docker installed)


        ShowSave.show();
        ShowSelect.show();
        ShowCache.show();
        ShowUpdate.show();

//        ShowQueryAndFluent.showQuery();
//        ShowQueryAndFluent.showFluent();

//        ShowDelete.show();





    }



}

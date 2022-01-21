package demoapp;

import demoapp.models.Person;
import demoapp.models.Student;
import demoapp.samples.ShowQueryAndFluent;
import demoapp.samples.ShowSave;
import demoapp.samples.ShowSelect;
import demoapp.samples.ShowUpdate;
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

        ShowQueryAndFluent.showQuery();
        ShowQueryAndFluent.showFluent();

//        ShowSave.show();
//        ORM.setCache(new Cache());
//        ShowSelect.show();
//
//        ShowUpdate.show();






    }



}

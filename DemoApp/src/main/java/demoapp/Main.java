package demoapp;

import demoapp.samples.ShowSave;
import demoapp.samples.ShowSelect;
import orm.Cache;
import orm.ORM;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;


public class Main {

    private static Map<Class<?>, Map<String,Object>> enums = new HashMap<>();

    
    public static void main(String[] args){

        //start database (only works with docker installed)

        ShowSave.show();
        ORM.setCache(new Cache());
        ShowSelect.show();






    }



}

package demoapp;

import demoapp.samples.ShowSave;
import demoapp.samples.ShowSelect;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;


public class Main {

    private static Map<Class<?>, Map<String,Object>> enums = new HashMap<>();

    
    public static void main(String[] args){

        //start database (only works with docker installed)

        List<String> list = new ArrayList<>();

        Class<?> c = list.getClass();

        try {
            var newlist = (Collection<Object>)c.getConstructor().newInstance();
            Object o = new String("test");

            newlist.add(o);


            System.out.println(new ArrayList<>(newlist));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

//        ShowSave.show();
//        ShowSelect.show();






    }



}

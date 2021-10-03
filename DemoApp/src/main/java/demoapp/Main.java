package demoapp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Main {

    
    public static void main(String[] args){
        Class<?> c = Student.class;
        List<Field> fields = new ArrayList<>();
        while(c != null /*&& !c.equals(Object.class)*/){
            fields.addAll(List.of(c.getDeclaredFields()));
            c=c.getSuperclass();
        }
        for(var field : fields){
            System.out.println(field.getName()+" - "+field.getType());
        }
    }
}

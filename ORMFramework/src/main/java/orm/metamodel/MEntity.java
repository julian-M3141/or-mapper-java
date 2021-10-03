package orm.metamodel;

import orm.annotations.Entity;
import orm.annotations.Ignore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MEntity {

    private MField primaryKey;
    private Class<?> member;
    private String tableName;
    private MField[] fields;

    public MEntity(Class<?> c) {
        //TODO annotate annotations for runtime availability
        var cattr = (Entity) c.getAnnotation(Entity.class);

        //set tablename, either the attribute is set, otherwise take the classname
        if(cattr == null || cattr.tableName() == null || cattr.tableName().equals("")){
            tableName = c.getSimpleName();
        }else{
            tableName = cattr.tableName();
        }

        //set member class
        member = c;


        //get all fields (even from parent class)
        List<Field> cfields = new ArrayList<>();

        Class<?> ctmp = c;
        while(ctmp != null /*&& !c.equals(Object.class)*/){
            cfields.addAll(List.of(ctmp.getDeclaredFields()));
            ctmp=ctmp.getSuperclass();
        }

        List<MField> mfields = new ArrayList<>();
        //TODO: maybe check if getter and setter are available
        for(var field : cfields){
            if(field.getAnnotation(Ignore.class)!=null) continue;
            var annotations = new __FieldAnnotation(field);
            MField mField = new MField(this, field.getName());
            mField.setColumnType(annotations.getColumnType());
            mField.setColumnName(annotations.getColumnName());
            if(annotations.isPK()){
                primaryKey = mField;
                mField.setPK(true);
            }
            if(annotations.isFK()){
                mField.setFK(true);
            }
            mField.setFieldType(field.getType());
            mField.setNullable(annotations.isNullable());
            mfields.add(mField);
        }

        fields = mfields.toArray(new MField[0]);


    }
}

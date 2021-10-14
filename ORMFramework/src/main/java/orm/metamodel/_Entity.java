package orm.metamodel;

import orm.annotations.Entity;
import orm.annotations.Ignore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class _Entity {

    private _Field primaryKey;

    private Class<?> member;

    private String tableName;
    private _Field[] fields;

    //TODO write own Exception for no getter and setter
    public _Entity(Class<?> c) throws NoSuchMethodException {
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

        List<_Field> mfields = new ArrayList<>();
        //TODO: maybe check if getter and setter are available
        for(var field : cfields){
            if(field.getAnnotation(Ignore.class)!=null) continue;
            var annotations = new __FieldAnnotation(field);
            _Field mField = new _Field(this, field.getName());
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
            var nameCapitalizzed = field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
            mField.setGetter(c.getMethod("get" + nameCapitalizzed));
            mField.setSetter(c.getMethod("set" + nameCapitalizzed));
            mfields.add(mField);
        }

        fields = mfields.toArray(new _Field[0]);


    }

    public _Field getPrimaryKey() {
        return primaryKey;
    }

    public Class<?> getMember() {
        return member;
    }

    public String getTableName() {
        return tableName;
    }

    public _Field[] getFields() {
        return fields;
    }
}

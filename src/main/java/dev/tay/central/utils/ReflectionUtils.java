package dev.tay.central.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ReflectionUtils {

    private static List<?> cachedList;

    public static String getGenericFromList(List<?> list) {
        cachedList = list;
        try {
            Field field = ReflectionUtils.class.getField("cachedList");
            Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                Type[] typeArr = pType.getActualTypeArguments();
                return ((Class) typeArr[0]).getSimpleName();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return "";
    }

}

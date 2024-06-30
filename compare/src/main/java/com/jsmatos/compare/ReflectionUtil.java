package com.jsmatos.compare;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReflectionUtil {

    @SneakyThrows
    public static List<FieldInfo> getFields(Class<?> clazz) {
        List<FieldInfo> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            FieldInfo info = new FieldInfo();
            info.setParentClass(clazz);
            field.setAccessible(true);
            info.setField(field);
            info.setLabel(field.getAnnotation(Label.class).value());
            info.setType(field.getType().getTypeName());
            // Check for and handle enumeration types
            if (field.getType().isEnum()) {
                info.setType("enum");
                info.setEnumValues(Arrays.stream(field.getType().getEnumConstants())
                        .map(Enum.class::cast)
                        .map(Enum::name)
                        .toArray(String[]::new));
            }
            // Handle collection types
            if (Collection.class.isAssignableFrom(field.getType())) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    Type actualType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                    Class<?> typeClass = Class.forName(actualType.getTypeName());
                    info.setCollectionElementType(typeClass);
                    List<FieldInfo> typeClassFields = getFields(typeClass);
//                    Work.fieldsByClass.putIfAbsent(typeClass, typeClassFields);
//                    Work.typeToInfo.put(typeClass,)
                    info.setNestedFields(typeClassFields);
                }
                info.setType("collection");
            }
            if (!isPrimitiveOrWrapper(field.getType()) && !field.getType().getPackage().getName().startsWith("java.")) {
                info.setNestedFields(getFields(field.getType()));
            }
            fields.add(info);
        }
        return fields;
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return (type.isPrimitive() && type != void.class) ||
                type.equals(Double.class) || type.equals(Float.class) ||
                type.equals(Long.class) || type.equals(Integer.class) ||
                type.equals(Short.class) || type.equals(Character.class) ||
                type.equals(Byte.class) || type.equals(Boolean.class);
    }
}

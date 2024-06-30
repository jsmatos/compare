package com.jsmatos.compare;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Work {
    final Map<Class<?>, List<FieldInfo>> fieldsByClass;
//    static final Map<Class<?>, FieldInfo> typeToInfo = new HashMap<>();

    public Work(List<FieldInfo> fields) {
        this.fieldsByClass = fields.stream().collect(Collectors.groupingBy(FieldInfo::getParentClass));
//        typeToInfo.putAll(fields.stream().collect(Collectors.toMap(e -> e.getField().getType(), e -> e, (first, second) -> first)));
    }

    String with(Object before, Object after, Object updated) {
        StringBuilder sb = new StringBuilder();
        Stream.of(before, after, updated).filter(Objects::nonNull).map(Object::getClass).findAny().ifPresent(clazz -> {
            List<FieldInfo> fieldInfos = fieldsByClass.get(clazz);
            Iterator<FieldInfo> it = fieldInfos.iterator();
            while (it.hasNext()) {
                FieldInfo fieldInfo = it.next();
                Object beforeValue = fieldInfo.getValue(before);
                Object afterValue = fieldInfo.getValue(after);
                Object updatedValue = fieldInfo.getValue(updated);
                String s = fieldInfo.toHtmlRow(0, beforeValue, afterValue, updatedValue);
                sb.append(s);
                if (it.hasNext()) {
                    sb.append("<tr style=\"border-bottom: 1px\"><td colspan=6><br/></td></tr>");
                }
            }
        });
        return sb.toString();
    }

}
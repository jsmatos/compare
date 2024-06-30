package com.jsmatos.compare;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
class FieldInfo {
    private Class<?> parentClass;
    private Field field;
    private String label;
    private String type;
    private List<FieldInfo> nestedFields;
    private String[] enumValues;
    private Class<?> collectionElementType;

    private boolean hasNestedFields() {
        return nestedFields != null && nestedFields.size() > 0;
    }

    boolean isCollection() {
        return collectionElementType != null;
    }

    private Map<Object, Object> toMap(Collection<? extends HasId> col) {
        if (col == null) return Collections.emptyMap();
        return col.stream().collect(Collectors.toMap(HasId::getId, e -> e));
    }

    String indent(int v) {
        return "&nbsp;".repeat(Math.max(0, v));
    }

    static String header() {
        return "<tr><td></td><td>before</td><td>→</td><td>after</td><td>→</td><td>updated</td></tr>";
    }

    public String toHtmlRow(final int indent, Object beforeValue, Object afterValue, Object updatedValue) {
        if (this.label.equals("Contact details")){
            System.out.println(this.label);
        }
        if (this.label.equals("Legal address")){
            System.out.println(this.label);
        }
        if (isCollection()) {
            return handleCollection(indent, beforeValue, afterValue, updatedValue);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");

        if (hasNestedFields()) {
            sb.append("<td colspan=6>").append(indent(indent)).append(label).append("</td>");
            /*if (!nestedFields.get(0).hasNestedFields()) {
                sb.append(header());
            }*/
        } else {
            Object beforeToDisplay = toDisplay(beforeValue);
            Object afterToDisplay = toDisplay(afterValue);
            sb.append("<td>").append(indent(indent)).append(label).append("</td>");
            sb.append("<td>").append(beforeToDisplay).append("</td>");
            sb.append("<td>").append("→").append("</td>");
            if (Objects.equals(beforeToDisplay, afterToDisplay)) {
                sb.append("<td>").append(afterToDisplay).append("</td>");
            } else {
                sb.append("<td style=\"background-color: salmon\">").append(afterToDisplay).append("</td>");
            }
            sb.append("<td>").append(updateArrow()).append("</td>");
            sb.append("<td>").append(updated(updatedValue)).append("</td>");
        }
        sb.append("</tr>");
        if (hasNestedFields()) {
            for (FieldInfo nestedField : nestedFields) {
                sb.append(
                        nestedField.toHtmlRow(
                                indent + 1,
                                nestedField.getValue(beforeValue),
                                nestedField.getValue(afterValue),
                                nestedField.getValue(updatedValue)
                        )
                );
            }
        }

        return sb.toString();
    }

    private String handleCollection(int indent, Object beforeValue, Object afterValue, Object updatedValue) {
        if (HasId.class.isAssignableFrom(collectionElementType)) {
            return handleCollectionWithComparableElements(indent, beforeValue, afterValue, updatedValue);
        }
        return handleCollectionWithNonComparableElements(indent, beforeValue, afterValue, updatedValue);

    }

    private String handleCollectionWithNonComparableElements(int indent, Object beforeValue, Object afterValue, Object updatedValue) {
        StringBuilder sb = new StringBuilder();
        Collection<Object> beforeCol = (Collection<Object>) beforeValue;
        Collection<Object> afterCol = (Collection<Object>) afterValue;

        Class<?> collectionElementType = getCollectionElementType();
        List<FieldInfo> fields = ReflectionUtil.getFields(collectionElementType);
        sb.append("<tr>");
        sb.append("<td colspan=6>");
        sb.append(label);
        sb.append("</td>");
        sb.append("</tr>");


        sb.append("<tr>");
        sb.append("<td colspan=6>");
        sb.append("Before");
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td colspan=6>");
        Iterator<Object> it = beforeCol.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            sb.append("<table>");
            for (FieldInfo fieldInfo : fields) {
                sb.append("<tr>");
                sb.append("<td>");
                sb.append(indent(indent)).append(fieldInfo.label);
                sb.append("</td>");
                sb.append("<td>");
                sb.append(fieldInfo.getValue(o));
                sb.append("</td>");
                sb.append("</tr>");
            }
            if (it.hasNext()) {
                sb.append("<tr>");
                sb.append("<td colspan=6>");
                sb.append("</br>");
                sb.append("</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }


        sb.append("<tr>");
        sb.append("<td colspan=6>");
        sb.append("After");
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td colspan=6>");
        it = afterCol.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            sb.append("<table>");
            for (FieldInfo fieldInfo : fields) {
                sb.append("<tr>");
                sb.append("<td>");
                sb.append(indent(indent)).append(fieldInfo.label);
                sb.append("</td>");
                sb.append("<td>");
                sb.append(fieldInfo.getValue(o));
                sb.append("</td>");
                sb.append("</tr>");
            }
            if (it.hasNext()) {
                sb.append("<tr>");
                sb.append("<td colspan=6>");
                sb.append("</br>");
                sb.append("</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }


        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

    private String handleCollectionWithComparableElements(int indent, Object beforeValue, Object afterValue, Object updatedValue) {
        StringBuilder sb = new StringBuilder();
        Collection<? extends HasId> beforeCol = (Collection<? extends HasId>) beforeValue;
        Collection<? extends HasId> afterCol = (Collection<? extends HasId>) afterValue;
        Collection<? extends HasId> updatedCol = (Collection<? extends HasId>) updatedValue;

        Map<Object, Object> beforeMap = toMap(beforeCol);
        Map<Object, Object> afterMap = toMap(afterCol);
        Map<Object, Object> updatedMap = toMap(updatedCol);

        Set<Object> keys = new HashSet<>();
        keys.addAll(beforeMap.keySet());
        keys.addAll(afterMap.keySet());
        keys.addAll(updatedMap.keySet());


        Class<?> collectionElementType = getCollectionElementType();
        List<FieldInfo> fields = ReflectionUtil.getFields(collectionElementType);
        Iterator<Object> it = keys.iterator();
        int index = 0;
        while (it.hasNext()) {
            Object key = it.next();
            sb.append("<tr>");
            sb.append("<td colspan=6>").append(indent(indent)).append(label).append("&nbsp;[").append(++index).append("]</td>");
            sb.append("</tr>");
//            if (!field.hasNestedFields()) {
//                sb.append(header());
//            }
            Object beforeValue1 = beforeMap.get(key);
            Object afterValue1 = afterMap.get(key);
            Object updatedValue1 = updatedMap.get(key);
            for (FieldInfo fieldInfo : fields) {
                sb.append(fieldInfo.toHtmlRow(indent + 1, fieldInfo.getValue(beforeValue1), fieldInfo.getValue(afterValue1), fieldInfo.getValue(updatedValue1)));
            }
            if (it.hasNext()) {
                sb.append("<tr style=\"border-bottom: 1px\"><td colspan=6><br/></td></tr>");
            }
        }

        return sb.toString();
    }

    @SneakyThrows
    Object getValue(Object from) {
        if (from == null) return null;
        return field.get(from);
    }

    Object toDisplay(Object input) {
        if (input == null) return "";
        return input;
    }

    boolean isUpdatable() {
        return getField().getAnnotation(Updatable.class) != null;
    }

    String updateArrow() {
        if (isUpdatable()) return "→";
        return "";
    }

    String updated(Object input) {
        if (isUpdatable()) {
            if (input == null) input = "";
            return String.format("<input type=\"text\" id=\"lname\" name=\"lname\" value=\"%s\">", input);
        }
        return "";
    }
}
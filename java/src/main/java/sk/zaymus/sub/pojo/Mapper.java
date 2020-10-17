package sk.zaymus.sub.pojo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper can map Java POJO to Map object and Map to Java POJO. Serialized and
 * deserialized attributes must have defined standard getters and setters. For
 * serialization / deserialization is used {@link java.lang.reflect} library.
 *
 * @author Mikulas Zaymus
 */
public class Mapper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Serializes Java POJO to Map.
     *
     * @param o Java POJO to be serialized
     * @return Map containing key-value pairs of attributes
     * @throws IOException exception while accessing getter methods
     */
    public static Map toMap(Object o) throws IOException {
        Map<String, Object> map = new HashMap<>();
        try {
            Class<?> clazz = o.getClass();
            Field[] fields = clazz.getDeclaredFields();
            Object value;
            Method method;
            String name;
            for (Field field : fields) {
                name = field.getName();
                method = clazz.getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
                value = method.invoke(o);
                if (value != null) {
                    map.put(field.getName(), value);
                }
            }
        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IOException(ex);
        }
        return map;
    }

    /**
     * Deserializes Map to Java POJO.
     *
     * @param <T> POJO data type
     * @param m Map containing key-value pairs of attributes
     * @param clazz Class of new POJO
     * @return POJO object
     * @throws IOException exception while accessing setter methods
     */
    public static <T> T fromMap(Map m, Class<T> clazz) throws IOException {
        try {
            T instance = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            Object value;
            Method method;
            String name;
            for (Field field : fields) {
                name = field.getName();
                value = m.get(field.getName());
                if (value != null) {
                    method = clazz.getMethod("set" + Character.toUpperCase(name.charAt(0)) + name.substring(1), field.getType());
                    method.invoke(instance, getValue(field.getType(), value));
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | SecurityException | NoSuchMethodException | ParseException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Creates String representation of non null object fields.
     *
     * @param o Java POJO to be serialized
     * @return object as String
     */
    public static String toString(Object o) {
        StringBuilder sb = new StringBuilder(512);
        sb.append("[\"");
        try {
            Class<?> clazz = o.getClass();
            Field[] fields = clazz.getDeclaredFields();
            Object value;
            Method method;
            String name;
            for (Field field : fields) {
                name = field.getName();
                method = clazz.getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
                value = method.invoke(o);
                if (value != null) {
                    sb.append(field.getName()).append("\":\"").append(value).append("\",\"");
                }
            }
            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }
        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            sb.setLength(0);
            return sb.append('[').append(ex.getMessage()).append(']').toString();
        }
        return sb.append(']').toString();
    }

    private static Object getValue(Class clazz, Object value) throws ParseException, IllegalArgumentException {
        if (String.class.equals(clazz)) {
            return value;
        } else if (Integer.class.equals(clazz)) {
            return Integer.parseInt((String) value);
        } else if (Double.class.equals(clazz)) {
            return Double.parseDouble((String) value);
        } else if (Date.class.equals(clazz)) {
            synchronized (sdf) {
                return sdf.parse((String) value);
            }
        }
        throw new IllegalArgumentException("Conversion for class [" + clazz.getName() + "] not configured.");
    }

}

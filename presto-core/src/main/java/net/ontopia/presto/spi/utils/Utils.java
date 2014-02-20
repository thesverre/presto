package net.ontopia.presto.spi.utils;

import java.util.List;

import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoType;

import org.codehaus.jackson.map.ObjectMapper;

public class Utils {

    public static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

    public static boolean different(Object o1, Object o2) {
        return !(o1 == null ? o2 == null : o1.equals(o2));
    }

    public static boolean equals(Object o1, Object o2) {
        return (o1 == null ? o2 == null : o1.equals(o2));
    }

    public static final <T> T newInstanceOf(String className, Class<T> type) {
        return newInstanceOf(className, type, true);
    }
    
    @SuppressWarnings("unchecked")
    public static final <T> T newInstanceOf(String className, Class<T> type, boolean warnIfDifferentType) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> klass = Class.forName(className, true, classLoader);
            if (type.isAssignableFrom(klass)) {
                return (T) klass.newInstance();
            } else {
                throw new RuntimeException("Class " + className + " not assignable to " + type);                    
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class " + className + " not found.");
        } catch (InstantiationException e) {
            throw new RuntimeException("Not able to instatiate class " + className + ".");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Not able to instatiate class " + className + " (illegal access).");
        }
    }

    public static void validateNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException("Object cannot be null");
        }
    }
    
    public static String getName(Object o) {
        if (o instanceof PrestoTopic) {
            PrestoTopic topic = (PrestoTopic)o;
            return topic.getName();
        } else {
            return o == null ? "null" : o.toString();
        }
    }
 
    public static String getName(PrestoField field, Object o) {
        if (o instanceof PrestoTopic) {
            PrestoTopic topic = (PrestoTopic)o;
            return topic.getName(field);
        } else {
            return o == null ? "null" : o.toString();
        }
    }

    public static PrestoType getTopicType(PrestoTopic topic, PrestoSchemaProvider schemaProvider) {
        String typeId = topic.getTypeId();
        return schemaProvider.getTypeById(typeId);
    }

    public static List<? extends Object> getFieldValues(PrestoTopic topic, String fieldId, PrestoSchemaProvider schemaProvider) {
        PrestoType type = getTopicType(topic, schemaProvider);
        PrestoField field = type.getFieldById(fieldId);
        return topic.getValues(field);
    }
    
}

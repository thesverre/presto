package net.ontopia.presto.spi.jackson;

import net.ontopia.presto.spi.PrestoField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JacksonDefaultDataStrategy implements JacksonDataStrategy {
    
    private static Logger log = LoggerFactory.getLogger(JacksonDefaultDataStrategy.class);
    
    private static final String ID_DEFAULT_FIELD = "_id";
    private static final String TYPE_DEFAULT_FIELD = ":type";
    private static final String NAME_DEFAULT_FIELD = ":name";

    @Override
    public String getId(ObjectNode doc) {
        return doc.get(ID_DEFAULT_FIELD).textValue();
    }
    
    @Override
    public String getTypeId(ObjectNode doc) {
        return doc.get(TYPE_DEFAULT_FIELD).textValue();
    }
    
    @Override
    public String getName(ObjectNode doc) {
        return getSingleStringFieldValue(doc, NAME_DEFAULT_FIELD);
    }
    
    protected String getSingleStringFieldValue(ObjectNode doc, String fieldId) {
        ArrayNode arrayNode = getFieldValueArrayNode(doc, fieldId);
        if (arrayNode == null) {
            return null;
        } else {
            return arrayNode.size() > 0 ? arrayNode.get(0).asText() : null;
        }
    }

    @Override
    public String getName(ObjectNode doc, PrestoField field) {
        return getName(doc);
    }

    @Override
    public ArrayNode getFieldValue(ObjectNode doc, PrestoField field) {
        return getFieldValueArrayNode(doc, field.getActualId());
    }

    private ArrayNode getFieldValueArrayNode(ObjectNode doc, String fieldId) {
        JsonNode value = doc.path(fieldId);
        if (value.isArray()) {
            return (ArrayNode)value;
        } else if (value.isMissingNode() || value.isNull()) {
            return null;
        } else {
            log.warn("Value " + value + " in field '" + fieldId + "' is not an array");
            return null;
        }
    }

    @Override
    public boolean hasFieldValue(ObjectNode doc, PrestoField field) {
        return doc.has(field.getActualId());
    }

    @Override
    public void putFieldValue(ObjectNode doc, PrestoField field, ArrayNode value) {
        doc.put(field.getActualId(), value);
    }
    
    @Override
    public void clearFieldValue(ObjectNode doc, PrestoField field) {
        doc.remove(field.getActualId());
    }

}

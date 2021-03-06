package net.ontopia.presto.jaxrs.process.impl;

import java.util.Collections;
import java.util.List;

import net.ontopia.presto.jaxb.Value;
import net.ontopia.presto.jaxrs.process.ValueFactory;
import net.ontopia.presto.spi.PrestoDataProvider;
import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.rules.PathExpressions;
import net.ontopia.presto.spi.utils.PatternValueUtils;
import net.ontopia.presto.spi.utils.PrestoContext;
import net.ontopia.presto.spi.utils.PrestoContextRules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ExpressionValueFactory extends ValueFactory {
    
    @Override
    public Value createValue(PrestoContextRules rules, PrestoField field, String value) {
        PrestoSchemaProvider schemaProvider = getSchemaProvider();
        ObjectNode config = getConfig();

        List<? extends Object> values = getValues(getDataProvider(), schemaProvider, 
                rules, field, getConfig());
        
        for (Object object : values) {
            PrestoTopic t = (PrestoTopic) object;
            if (t.getId().equals(value)) {
                Value result = new Value();
                result.setValue(value);
                result.setName(getName(schemaProvider, rules, field, t, config)); 
                return result;
            }
        }
        return null;
        
    }

    @Override
    public Value createValue(PrestoContextRules rules, PrestoField field, PrestoTopic value) {
        String topicId = value.getId();
        return createValue(rules, field, topicId);
    }

    public static List<? extends Object> getValues(PrestoDataProvider dataProvider, PrestoSchemaProvider schemaProvider, 
            PrestoContextRules rules,  PrestoField field, ObjectNode config) {
        String path = null;
        JsonNode pathNode = config.path("path");
        if (pathNode.isMissingNode()) {
            path = field.getInlineReference();
        } else {
            path = pathNode.textValue();
        }
        if (path == null) {
            return Collections.emptyList();
        }
        return PathExpressions.getValues(rules, path);
    }

    private String getName(PrestoSchemaProvider schemaProvider, 
            PrestoContextRules rules, PrestoField field, PrestoTopic topic, ObjectNode config) {
        JsonNode namePatternNode = config.path("name-pattern");
        if (namePatternNode.isTextual()) {
            String namePattern = namePatternNode.textValue();
            PrestoContext context = rules.getContext();
            return PatternValueUtils.getValueByPattern(schemaProvider, context, topic, namePattern);
        } else {
            return topic.getName(field);
        }
    }

}

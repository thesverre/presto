package net.ontopia.presto.spi.rules;

import net.ontopia.presto.spi.PrestoFieldUsage;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoType;
import net.ontopia.presto.spi.PrestoView;
import net.ontopia.presto.spi.utils.PrestoContext;
import net.ontopia.presto.spi.utils.PrestoContextRules.FieldValueFlag;
import net.ontopia.presto.spi.utils.Utils;

import org.codehaus.jackson.node.ObjectNode;

public class HasFieldValuesFieldValueRule extends BooleanFieldValueRule {

    @Override
    protected boolean getResult(FieldValueFlag flag, PrestoContext context, PrestoFieldUsage field, Object value, ObjectNode config) {
        if (context.isNewTopic()) {
            return false;
        } else {
            if (value instanceof PrestoTopic) {
                PrestoTopic topic = (PrestoTopic)value;
                PrestoType type = Utils.getTopicType(topic, getSchemaProvider());
                PrestoView view = field.getValueView(type);
                PrestoContext subContext = PrestoContext.createSubContext(context, field, topic, type, view);
                return HasFieldValues.hasFieldValues(getDataProvider(), getSchemaProvider(), subContext, config);
            }
            return false;
        }
    }
    
}
package net.ontopia.presto.jaxrs.process;

import net.ontopia.presto.jaxb.Value;
import net.ontopia.presto.jaxrs.AbstractHandler;
import net.ontopia.presto.jaxrs.PrestoContextRules;
import net.ontopia.presto.spi.PrestoFieldUsage;
import net.ontopia.presto.spi.PrestoTopic;

public abstract class ValueFactory extends AbstractHandler {

    public Value createValue(PrestoContextRules rules, PrestoFieldUsage field, String value) {
        Value result = new Value();
        result.setValue(value);
        return result;
    }

    public Value createValue(PrestoContextRules rules, PrestoFieldUsage field, PrestoTopic value) {
        Value result = new Value();
        result.setValue(value.getId());
        result.setName(value.getName(field));
        return result;
    }

}

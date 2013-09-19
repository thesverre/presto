package net.ontopia.presto.spi.resolve;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.ontopia.presto.spi.PrestoDataProvider;
import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoTopic.PagedValues;
import net.ontopia.presto.spi.PrestoTopic.Paging;
import net.ontopia.presto.spi.utils.PrestoPagedValues;
import net.ontopia.presto.spi.utils.PrestoVariableResolver;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public class PrestoCoalesceResolver extends PrestoFieldResolver {

    @Override
    public PagedValues resolve(Collection<? extends Object> objects,
            PrestoField field, boolean isReference, Paging paging, PrestoVariableResolver variableResolver) {

        ObjectNode config = getConfig();

        List<? extends Object> result = null;

        if (config != null && config.has("resolve")) {
            JsonNode resolveParentConfig = config.get("resolve");
            if (resolveParentConfig.isArray()) {
                PrestoDataProvider dataProvider = getDataProvider();
                for (JsonNode resolveConfig : resolveParentConfig) {
                    PagedValues values = dataProvider.resolveValues(objects, field, paging, resolveConfig, variableResolver);
                    result = values.getValues();
                    if (result != null && !result.isEmpty()) {
                        break;
                    }
                }
            }
        }
        if (result != null) {
            return new PrestoPagedValues(result, paging, 0);
        } else {
            return new PrestoPagedValues(Collections.emptyList(), paging, 0);
        }
    }
    
}
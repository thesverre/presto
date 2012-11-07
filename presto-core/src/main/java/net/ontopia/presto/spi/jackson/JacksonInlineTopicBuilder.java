package net.ontopia.presto.spi.jackson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.node.ObjectNode;

import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoInlineTopicBuilder;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoType;

public class JacksonInlineTopicBuilder implements PrestoInlineTopicBuilder {

    private JacksonDataProvider dataProvider;
    private PrestoType type;
    private String topicId;
    private Map<PrestoField,Collection<?>> fields = new HashMap<PrestoField,Collection<?>>();
    
    public JacksonInlineTopicBuilder(JacksonDataProvider dataProvider, PrestoType type, String topicId) {
        this.dataProvider = dataProvider;
        this.type = type;
        this.topicId = topicId;
    }

    @Override
    public void setField(PrestoField field, Collection<?> values) {
        fields.put(field, values);
    }

    @Override
    public PrestoTopic build() {
        ObjectNode data = dataProvider.createObjectNode(type, topicId);
        JacksonInlineTopic result = new JacksonInlineTopic(dataProvider, data);
        for (Entry<PrestoField,Collection<?>> e : fields.entrySet()) {
            PrestoField field = e.getKey();
            Collection<?> values = e.getValue();
            result.setValue(field, values);
        }
        return result;
    }
    
}

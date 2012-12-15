package net.ontopia.presto.jaxrs.process.impl;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import net.ontopia.presto.jaxb.FieldData;
import net.ontopia.presto.jaxrs.process.FieldDataProcessor;
import net.ontopia.presto.spi.PrestoFieldUsage;
import net.ontopia.presto.spi.PrestoTopic;

public class FieldMessagesPostProcessor extends FieldDataProcessor {

    @Override
    public FieldData processFieldData(FieldData fieldData, PrestoTopic topic, PrestoFieldUsage field) {
        ObjectNode extraNode = getPresto().getFieldExtraNode(field);
        if (extraNode != null) {
            JsonNode messagesNode = extraNode.path("messages");
            if (messagesNode.isArray()) {
                List<FieldData.Message> messages = new ArrayList<FieldData.Message>();
                for (JsonNode messageNode : messagesNode) {
                    String type = messageNode.get("type").getTextValue();
                    String message = messageNode.get("message").getTextValue();
                    messages.add(new FieldData.Message(type, message));
                }
                if (fieldData.getMessages() != null) {
                    fieldData.getMessages().addAll(messages);
                } else {
                    fieldData.setMessages(messages);
                }
            }
        }
        return fieldData;
    }

}

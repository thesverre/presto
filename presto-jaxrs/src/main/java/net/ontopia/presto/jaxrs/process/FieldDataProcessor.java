package net.ontopia.presto.jaxrs.process;

import java.text.MessageFormat;

import net.ontopia.presto.jaxb.FieldData;
import net.ontopia.presto.jaxrs.PrestoContext;
import net.ontopia.presto.spi.PrestoFieldUsage;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public abstract class FieldDataProcessor extends AbstractProcessor {
    
    public abstract FieldData processFieldData(FieldData fieldData, PrestoContext context, PrestoFieldUsage field);

    protected String getErrorMessage(String errorId, PrestoFieldUsage field, String defaultErrorMessage, Object... args) {
        String errorMessage = getErrorMessageConfig(errorId, field, defaultErrorMessage);
        if (args != null && args.length > 0) {
            return MessageFormat.format(errorMessage, args);
        } else {
            return errorMessage;
        }
    }
    
    private String getErrorMessageConfig(String errorId, PrestoFieldUsage field, String defaultErrorMessage) {
        ObjectNode config = getConfig();
        if (config != null) {
            JsonNode errorNode = config.path("error-messages").path(errorId);
            if (errorNode.isTextual()) {
                return errorNode.getTextValue();
            }
        }
        return defaultErrorMessage;
    }

}

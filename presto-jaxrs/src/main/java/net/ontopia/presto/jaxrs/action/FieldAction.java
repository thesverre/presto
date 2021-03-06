package net.ontopia.presto.jaxrs.action;

import net.ontopia.presto.jaxb.TopicView;
import net.ontopia.presto.jaxrs.Presto;
import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoTopic.Projection;
import net.ontopia.presto.spi.utils.PrestoContext;
import net.ontopia.presto.spi.utils.PrestoContextRules;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class FieldAction {

    private ObjectNode config;

    private Presto presto;

    public abstract boolean isActive(PrestoContextRules rules, PrestoField field, Projection projection, String actionId);
    
    public abstract TopicView executeAction(PrestoContext context, TopicView topicView, PrestoField field, String actionId);

    public ObjectNode getConfig() {
        return config;
    }

    public void setConfig(ObjectNode config) {
        this.config = config;
    }

    public Presto getPresto() {
        return presto;
    }

    public void setPresto(Presto presto) {
        this.presto = presto;
    }
    
}

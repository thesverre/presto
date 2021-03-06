package net.ontopia.presto.spi.utils;

import java.util.Collection;
import java.util.List;

import net.ontopia.presto.spi.PrestoDataProvider;
import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoTopic.PagedValues;
import net.ontopia.presto.spi.PrestoTopic.Projection;
import net.ontopia.presto.spi.PrestoType;
import net.ontopia.presto.spi.PrestoView;
import net.ontopia.presto.spi.resolve.PrestoResolver;

import com.fasterxml.jackson.databind.JsonNode;

public class PrestoContext {

    private static final String NEW_TOPICID_PREFIX = "_";

    private final String topicId;
    
    private final PrestoTopic topic;
    private final PrestoType type;
    private final PrestoView view;

    private final boolean isNewTopic;
    
    private PrestoContext parentContext;
    private PrestoField parentField;

    private final PrestoResolver resolver;
    
    private PrestoContext(PrestoResolver resolver, String topicId, String viewId) {
        this.resolver = resolver;
        PrestoDataProvider dataProvider = resolver.getDataProvider();
        PrestoSchemaProvider schemaProvider = resolver.getSchemaProvider();
        if (topicId == null) {
            throw new RuntimeException("topicId cannot be null");
        } else if (isNewTopic(topicId)) {
            type = getTypeOfNewTopic(topicId, schemaProvider);
            topic = null;
            isNewTopic = true;
        } else {
            PrestoTopic t = dataProvider.getTopicById(topicId);
            if (t != null) {
                type = schemaProvider.getTypeById(t.getTypeId());
            } else {
                type = null;
            }
            topic = t;
            isNewTopic = false;
        }
        if (type != null) {
            if (viewId == null) {
                view = type.getDefaultView();
            } else {
                view = type.getViewById(viewId);
            }
        } else {
            view = null;
        }
        this.topicId = topicId;
    }

    private PrestoContext(PrestoResolver resolver, PrestoType type, PrestoView view) {
        this(resolver, null, type, view);
    }

    private PrestoContext(PrestoResolver resolver, PrestoTopic topic, PrestoType type, PrestoView view) {
        this.resolver = resolver;
        this.topic = topic;
        this.topicId = (topic == null ? NEW_TOPICID_PREFIX + type.getId() : topic.getId());
        this.type = type;
        this.view = view;
        this.isNewTopic = (topic == null);
    }
    
    // create new contexts
    
    public static PrestoContext create(PrestoResolver resolver, String topicId, String viewId) {
        return new PrestoContext(resolver, topicId, viewId);
    }
    
    public static PrestoContext create(PrestoResolver resolver, PrestoTopic topic) {
        String typeId = topic.getTypeId();
        PrestoSchemaProvider schemaProvider = resolver.getSchemaProvider();
        PrestoType type = schemaProvider.getTypeById(typeId);
        return new PrestoContext(resolver, topic, type, type.getDefaultView());
    }
    
    public static PrestoContext create(PrestoResolver resolver, PrestoType type, PrestoView view) {
        return new PrestoContext(resolver, type, view);
    }
    
    public static PrestoContext create(PrestoResolver resolver, PrestoTopic topic, PrestoType type, PrestoView view) {
        return new PrestoContext(resolver, topic, type, view);
    }
    
    public static PrestoContext newContext(PrestoContext context, PrestoTopic topic) {
        PrestoContext parentContext = context.getParentContext();
        PrestoField parentField = context.getParentField();
        PrestoType type = context.getType();
        PrestoView view = context.getView();
        PrestoContext result = new PrestoContext(context.getResolver(), topic, type, view);
        result.setParentContext(parentContext, parentField);
        return result;
    }
    
    public static PrestoContext newContext(PrestoContext context, String viewId) {
        PrestoContext parentContext = context.getParentContext();
        PrestoField parentField = context.getParentField();
        PrestoContext result;
        if (context.isNewTopic()) {
            String topicId = context.getTopicId();
            result = new PrestoContext(context.getResolver(), topicId, viewId);
        } else {
            PrestoTopic topic = context.getTopic();
            PrestoType type = context.getType();
            PrestoView view = type.getViewById(viewId);
            result = new PrestoContext(context.getResolver(), topic, type, view);
        }
        result.setParentContext(parentContext, parentField);
        return result;
    }
    
    // create subcontexts
    
    public static PrestoContext createSubContext(PrestoContext parentContext, PrestoField parentField, PrestoTopic topic) {
        // ISSUE: shouldn't the view be parentField.getValueView(type) instead of type.getDefaultView()
        PrestoContext context = create(parentContext.getResolver(), topic);
        context.setParentContext(parentContext, parentField);
        return context;
    }
    
    public static PrestoContext createSubContext(PrestoContext parentContext, PrestoField parentField, PrestoType type, PrestoView view) {
        PrestoContext context = new PrestoContext(parentContext.getResolver(), type, view);
        context.setParentContext(parentContext, parentField);
        return context;
    }
    
    public static PrestoContext createSubContext(PrestoContext parentContext, PrestoField parentField, PrestoTopic topic, PrestoType type, PrestoView view) {
        PrestoContext context = new PrestoContext(parentContext.getResolver(), topic, type, view);
        context.setParentContext(parentContext, parentField);
        return context;
    }
    
    public static PrestoContext createSubContext(PrestoContext parentContext, PrestoField parentField, String topicId, String viewId) {
        PrestoContext context = new PrestoContext(parentContext.getResolver(), topicId, viewId);
        context.setParentContext(parentContext, parentField);
        return context;
    }

    // methods
    
    public static boolean isNewTopic(String topicId) {
        return topicId.startsWith(NEW_TOPICID_PREFIX);
    }
 
    public static PrestoType getTypeOfNewTopic(String topicId, PrestoSchemaProvider schemaProvider) {
        String typeId = topicId.substring(1);
        return schemaProvider.getTypeById(typeId);
    }
    
    public static String getTypeId(String topicId) {
        if (topicId != null && topicId.startsWith(NEW_TOPICID_PREFIX)) {
            String typeId = topicId.substring(1);
            return typeId; 
        }
        return null;
    }
    
    public PrestoContext getParentContext() {
        return parentContext;
    }
    
    public PrestoField getParentField() {
        return parentField;
    }
    
    private void setParentContext(PrestoContext parentContext, PrestoField parentField) {
        this.parentContext = parentContext;
        this.parentField = parentField;
    }

    public boolean isMissingTopic() {
        return topic == null && !isNewTopic;
    }
    
    public boolean isNewTopic() {
        return isNewTopic;
    }

    public PrestoTopic getTopic() {
        return topic;
    }
    
    public String getTopicId() {
        return topicId;
    }
    
    public PrestoType getType() {
        return type;
    }
    
    public PrestoView getView() {
        return view;
    }

    public PrestoField getFieldById(String fieldId) {
        return type.getFieldById(fieldId, view);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (parentContext != null) {
            sb.append(parentContext.toString());
            sb.append("\n $(");
            sb.append(parentField.getId());
            sb.append(")");
        }
        sb.append(topicId);
        sb.append("/");
        sb.append(view.getId());
        return sb.toString();
    }
    
    public PrestoResolver getResolver() {
        return resolver;
    }
    
    public List<? extends Object> resolveValues(PrestoField field) {
        return resolver.resolveValues(this, field);
    }

    public PagedValues resolveValues(PrestoField field, Projection projection) {
        return resolver.resolveValues(this, field, projection);
    }
    
    public PagedValues resolveValues(Collection<? extends Object> objects,
            PrestoField field, Projection projection, JsonNode resolveConfig, PrestoVariableResolver variableResolver) {
        return resolver.resolveValues(objects, field, projection, resolveConfig, variableResolver);
    }
    
}

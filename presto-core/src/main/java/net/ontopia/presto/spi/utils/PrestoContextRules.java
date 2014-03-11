package net.ontopia.presto.spi.utils;

import net.ontopia.presto.spi.PrestoDataProvider;
import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoTopic.Paging;
import net.ontopia.presto.spi.PrestoType;
import net.ontopia.presto.spi.PrestoView;
import net.ontopia.presto.spi.rules.DelegatingContextRules;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public abstract class PrestoContextRules {

    public enum TypeFlag {
        isReadOnlyType,
        isUpdatableType,
        isCreatableType,
        isRemovableType, 
        isDeletableType
    }

    public enum ViewFlag {
        isHiddenView
    }

    public enum FieldFlag {
        isHiddenField,
        isTraverableField,
        isSortedField,
        isSortedAscendingField,
        isPageableField,
        isReadOnlyField,
        isEditableField,
        isCreatableField,
        isAddableField,
        isRemovableField, 
        isMovableField
    }

    public enum FieldValueFlag {
        isAddableFieldValue,
        isRemovableFieldValue,
        isMovableFieldValue,
        isEditableFieldValue,
        isStorableFieldValue
    }

    public static interface TypeRule extends Handler {

        public Boolean getValue(TypeFlag flag, PrestoContextRules rules);
        
    }

    public static interface ViewRule extends Handler {

        public Boolean getValue(ViewFlag flag, PrestoContextRules rules, PrestoView view);
        
    }

    public static interface FieldRule extends Handler {

        public Boolean getValue(FieldFlag flag, PrestoContextRules rules, PrestoField field);

    }

    public static interface FieldValueRule extends Handler {

        public Boolean getValue(FieldValueFlag flag, PrestoContextRules rules, PrestoField field, Object value);

    }

    public static abstract class ContextRulesHandler extends AbstractHandler implements TypeRule, ViewRule, FieldRule, FieldValueRule {
    }

    private ContextRulesHandler handler;
    private boolean readOnlyType;

    private PrestoContext context;
    private PrestoType type;

    public PrestoContextRules(PrestoDataProvider dataProvider, PrestoSchemaProvider schemaProvider, PrestoContext context) {
        this.context = context;
        this.type = context.getType();

        ObjectNode config = null;

        ObjectNode extra = (ObjectNode)type.getExtra();
        if (extra != null) {
            JsonNode contextRules = extra.path("contextRules");
            if (contextRules.isObject()) {
                this.handler = AbstractHandler.getHandler(dataProvider, schemaProvider, ContextRulesHandler.class, contextRules);
                config = (ObjectNode)contextRules;
            }
        }
        if (this.handler == null) {
            this.handler = new DelegatingContextRules();
            this.handler.setConfig(config);
            this.handler.setDataProvider(dataProvider);;
            this.handler.setSchemaProvider(schemaProvider);
        }
        this.readOnlyType = isTypeHandlerFlag(TypeFlag.isReadOnlyType, false);
    }

    public PrestoContext getContext() {
        return context;
    }

    private boolean isTypeHandlerFlag(TypeFlag flag, boolean defaultValue) {
        Boolean result = handler.getValue(flag, this);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

    private boolean isViewHandlerFlag(ViewFlag flag, PrestoView view, boolean defaultValue) {
        Boolean result = handler.getValue(flag, this, view);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

    private boolean isFieldHandlerFlag(FieldFlag flag, PrestoField field, boolean defaultValue) {
        Boolean result = handler.getValue(flag, this, field);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

    private boolean isFieldValueHandlerFlag(FieldValueFlag flag, PrestoField field, Object value, boolean defaultValue) {
        Boolean result = handler.getValue(flag, this, field, value);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

    public boolean isReadOnlyType() {
        return readOnlyType;
    }

    public boolean isUpdatableType() {
        return isTypeHandlerFlag(TypeFlag.isUpdatableType, true);
    }
    
    public boolean isCreatableType() {
        return isTypeHandlerFlag(TypeFlag.isCreatableType, type.isCreatable());
    }
    
    public boolean isRemovableType() {
        return isTypeHandlerFlag(TypeFlag.isRemovableType, type.isRemovable());
    }
    
    public boolean isDeletableType() {
        return isTypeHandlerFlag(TypeFlag.isDeletableType, type.isRemovable());
    }
    
    public boolean isHiddenView(PrestoView view) {
        return isViewHandlerFlag(ViewFlag.isHiddenView, view, false);
    }    

    // characteristics

    //    public boolean isInline(PrestoField field) {
    //        return field.isInline();
    //    }
    //
    //    public boolean isEmbedded(PrestoField field) {
    //        return field.isEmbedded();
    //    }

    public boolean isHiddenField(PrestoField field) {
        return isFieldHandlerFlag(FieldFlag.isHiddenField, field, field.isHidden());
    }

    public boolean isTraversableField(PrestoField field) {
        return isFieldHandlerFlag(FieldFlag.isTraverableField, field, field.isTraversable());
    }

    public boolean isSortedField(PrestoField field) {
        return isFieldHandlerFlag(FieldFlag.isSortedField, field, field.isSorted());
    }

    public boolean isSortedAscendingField(PrestoField field) {
        return isFieldHandlerFlag(FieldFlag.isSortedAscendingField, field, field.isSortedAscending());
    }

    public boolean isPageableField(PrestoField field) {
        return isFieldHandlerFlag(FieldFlag.isPageableField, field, field.isPageable());
    }

    // mutability

    public boolean isReadOnlyField(PrestoField field) {
        return isReadOnlyType() || isFieldHandlerFlag(FieldFlag.isReadOnlyField, field, field.isReadOnly());
    }

    public boolean isEditableField(PrestoField field) {
        return isFieldHandlerFlag(FieldFlag.isEditableField, field, field.isEditable());
    }

    public boolean isCreatableField(PrestoField field) {
        return isFieldHandlerFlag(FieldFlag.isCreatableField, field, field.isCreatable());
    }

    public boolean isAddableField(PrestoField field) {
        return isFieldHandlerFlag(FieldFlag.isAddableField, field, field.isAddable());
    }

    public boolean isRemovableField(PrestoField field) {
        return isFieldHandlerFlag(FieldFlag.isRemovableField, field, field.isRemovable());
    }

    public boolean isMovableField(PrestoField field) {
        return isFieldHandlerFlag(FieldFlag.isMovableField, field, field.isMovable());
    }

    public boolean isAddableFieldValue(PrestoField field, Object value) {
        return isFieldValueHandlerFlag(FieldValueFlag.isAddableFieldValue, field, value, field.isAddable());
    }

    public boolean isRemovableFieldValue(PrestoField field, Object value) {
        return isFieldValueHandlerFlag(FieldValueFlag.isRemovableFieldValue, field, value, field.isRemovable());
    }

    public boolean isMovableFieldValue(PrestoField field, Object value) {
        return isFieldValueHandlerFlag(FieldValueFlag.isMovableFieldValue, field, value, field.isMovable());
    }

    public boolean isEditableFieldValue(PrestoField field, Object value) {
        return isFieldValueHandlerFlag(FieldValueFlag.isEditableFieldValue, field, value, field.isEditable());
    }

    public boolean isStorableFieldValue(PrestoField field, Object value) {
        return isFieldValueHandlerFlag(FieldValueFlag.isStorableFieldValue, field, value, true);
    }

    //    public boolean isCascadingDeleteField(PrestoField field) {
    //        return field.isCascadingDelete();
    //    }

    protected abstract PrestoAttributes getAttributes();

    public abstract PrestoContextRules getPrestoContextRules(PrestoContext context);
//
//    private PrestoContextRules createSubContextRules(PrestoContext parentContext, PrestoField parentField, PrestoTopic topic, PrestoType type, PrestoView view) {
//        PrestoContext subContext = PrestoContext.createSubContext(parentContext, parentField, topic, type, view);
//        return getPrestoContextRules(subContext);
//    }
//
//    public abstract PrestoContextRules getParentContextRules();
//
//    public abstract PrestoContextRules createSubContextRules(PrestoContext parentContext, PrestoField parentField, PrestoTopic topic, PrestoType type, PrestoView view);

    public FieldValues getFieldValues(PrestoField field) {
        return getFieldValues(field, 0, FieldValues.DEFAULT_LIMIT);
    }
    
    public FieldValues getFieldValues(PrestoField field, int offset, int limit) {
        if (context.isNewTopic()) {
            return getDefaultFieldValues(field);
        } else {
            PrestoTopic topic = context.getTopic();

            // server-side paging (only if not sorting)
            if (isPageableField(field) && !isSortedField(field)) {
                int actualOffset = offset >= 0 ? offset : 0;
                int actualLimit = limit > 0 ? limit : FieldValues.DEFAULT_LIMIT;
                PrestoTopic.PagedValues pagedValues = topic.getValues(field, actualOffset, actualLimit);
                Paging paging = pagedValues.getPaging();
                return FieldValues.create(pagedValues.getValues(), paging.getOffset(), paging.getLimit(), pagedValues.getTotal());
            } else {
                return FieldValues.create(topic.getValues(field));
            }
        }
    }
    
    protected FieldValues getDefaultFieldValues(PrestoField field) {
        ObjectNode extra = ExtraUtils.getFieldExtraNode(field);
        if (extra != null) {
            JsonNode defNode = extra.path("assignDefaultValues");
            if (defNode.isTextual()) {
                String key = defNode.getTextValue();
                PrestoAttributes attributes = getAttributes();
                Object defaultValues = attributes.getAttribute(key);
                if (defaultValues instanceof FieldValues) {
                    return (FieldValues) defaultValues;
                }
            }
        }
        return FieldValues.EMPTY; // TODO: support initial values;   
    }

}

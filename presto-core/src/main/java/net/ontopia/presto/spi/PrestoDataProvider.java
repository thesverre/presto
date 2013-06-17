package net.ontopia.presto.spi;

import java.util.Collection;

public interface PrestoDataProvider {

    String getProviderId();
    
    PrestoTopic getTopicById(String topicId);

    Collection<PrestoTopic> getTopicsByIds(Collection<String> topicIds);

    Collection<? extends Object> getAvailableFieldValues(PrestoTopic topic, PrestoFieldUsage field, String query);

    PrestoChangeSet newChangeSet();

    PrestoChangeSet newChangeSet(ChangeSetHandler handler);

    PrestoInlineTopicBuilder createInlineTopic(PrestoType type, String topicId);

    void close();

    public static interface ChangeSetHandler {

        public void onBeforeSave(PrestoChangeSet changeSet, PrestoChanges changes);

        public void onAfterSave(PrestoChangeSet changeSet, PrestoChanges changes);
        
    }
    
}

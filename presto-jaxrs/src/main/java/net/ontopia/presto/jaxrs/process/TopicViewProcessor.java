package net.ontopia.presto.jaxrs.process;

import net.ontopia.presto.jaxb.TopicView;
import net.ontopia.presto.jaxrs.PrestoContextRules;

public abstract class TopicViewProcessor extends AbstractProcessor {

    public abstract TopicView processTopicView(TopicView topicView, PrestoContextRules rules);

}

package net.ontopia.presto.spi.resolve;

import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.node.ObjectNode;

import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoTopic.Paging;
import net.ontopia.presto.spi.utils.PrestoVariableContext;

public interface PrestoResolverFunction {

    List<Object> execute(PrestoVariableContext context, ObjectNode config, Collection<? extends Object> objects, 
            PrestoField field, Paging paging);

}
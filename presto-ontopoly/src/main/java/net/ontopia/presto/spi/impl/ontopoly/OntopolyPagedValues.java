package net.ontopia.presto.spi.impl.ontopoly;

import java.util.List;

import net.ontopia.presto.spi.PrestoTopic.PagedValues;

public class OntopolyPagedValues implements PagedValues {

    private final List<Object> values;
    private final int offset;
    private final int limit;
    private final int total;

    OntopolyPagedValues(List<Object> values, int offset, int limit, int total) {
        this.values = values;
        this.offset = offset;
        this.limit = limit;
        this.total = total;
    }

    public List<Object> getValues() {
        return values;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public int getTotal() {
        return total;
    }

}
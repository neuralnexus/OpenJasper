package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import org.apache.commons.collections.OrderedMap;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
public class ResultsOrderedMap {
    private final OrderedMap orderedMap;
    private final boolean skipCriteriaSearch;

    private ResultsOrderedMap(OrderedMap orderedMap, boolean skipCriteriaSearch) {
        this.orderedMap = orderedMap;
        this.skipCriteriaSearch = skipCriteriaSearch;
    }

    public OrderedMap getOrderedMap() {
        return orderedMap;
    }

    public boolean isSkipCriteriaSearch() {
        return skipCriteriaSearch;
    }

    public static class Builder {
        private OrderedMap orderedMap;
        private boolean skipCriteriaSearch;

        public Builder setOrderedMap(OrderedMap orderedMap) {
            this.orderedMap = orderedMap;
            return this;
        }

        public Builder setSkipCriteriaSearch(boolean skipCriteriaSearch) {
            this.skipCriteriaSearch = skipCriteriaSearch;
            return this;
        }

        public ResultsOrderedMap build() {
            return new ResultsOrderedMap(orderedMap, skipCriteriaSearch);
        }
    }
}

/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.data.provider.hierarchy;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.FilterUtils;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalFilterUtils.HierarchialConfigurableFilterDataProviderWrapper;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalFilterUtils.HierarchicalFilterDataProviderWrapper;
import com.vaadin.flow.function.SerializableBiFunction;
import com.vaadin.flow.function.SerializableFunction;

/**
 * A common interface for fetching hierarchical data from a data source, such as
 * an in-memory collection or a backend database.
 *
 * @author Vaadin Ltd
 *
 * @param <T>
 *            data type
 * @param <F>
 *            filter type
 * @since 1.2
 */
public interface HierarchicalDataProvider<T, F> extends DataProvider<T, F> {

    /**
     * Get the number of immediate child data items for the parent item returned
     * by a given query.
     *
     * @param query
     *            given query to request the count for
     * @return the count of child data items for the data item
     *         {@link HierarchicalQuery#getParent()}
     *
     * @throws IllegalArgumentException
     *             if the query is not of type HierarchicalQuery
     */
    @Override
    public default int size(Query<T, F> query) {
        if (query instanceof HierarchicalQuery<?, ?>) {
            return getChildCount((HierarchicalQuery<T, F>) query);
        }
        throw new IllegalArgumentException(
                "Hierarchical data provider doesn't support non-hierarchical queries");
    }

    /**
     * Fetches data from this HierarchicalDataProvider using given
     * {@code query}. Only the immediate children of
     * {@link HierarchicalQuery#getParent()} will be returned.
     *
     * @param query
     *            given query to request data with
     * @return a stream of data objects resulting from the query
     *
     * @throws IllegalArgumentException
     *             if the query is not of type HierarchicalQuery
     */
    @Override
    public default Stream<T> fetch(Query<T, F> query) {
        if (query instanceof HierarchicalQuery<?, ?>) {
            return fetchChildren((HierarchicalQuery<T, F>) query);
        }
        throw new IllegalArgumentException(
                "Hierarchical data provider doesn't support non-hierarchical queries");
    }

    /**
     * Get the number of immediate child data items for the parent item returned
     * by a given query.
     *
     * @param query
     *            given query to request the count for
     * @return the count of child data items for the data item
     *         {@link HierarchicalQuery#getParent()}
     */
    public int getChildCount(HierarchicalQuery<T, F> query);

    /**
     * Fetches data from this HierarchicalDataProvider using given
     * {@code query}. Only the immediate children of
     * {@link HierarchicalQuery#getParent()} will be returned.
     *
     * @param query
     *            given query to request data with
     * @return a stream of data objects resulting from the query
     */
    public Stream<T> fetchChildren(HierarchicalQuery<T, F> query);

    /**
     * Check whether a given item has any children associated with it.
     *
     * @param item
     *            the item to check for children
     * @return whether the given item has children
     */
    public boolean hasChildren(T item);

    @SuppressWarnings("serial")
    @Override
    default <Q, C> HierarchicalConfigurableFilterDataProvider<T, Q, C> withConfigurableFilter(
            SerializableBiFunction<Q, C, F> filterCombiner) {
        return new HierarchialConfigurableFilterDataProviderWrapper<T, Q, C, F>(
                this) {
            @Override
            protected F combineFilters(Q queryFilter, C configuredFilter) {
                return FilterUtils.combineFilters(filterCombiner, queryFilter,
                        configuredFilter);
            }
        };
    }

    @Override
    @SuppressWarnings("serial")
    default <C> HierarchicalDataProvider<T, C> withConvertedFilter(
            SerializableFunction<C, F> filterConverter) {
        return new HierarchicalFilterDataProviderWrapper<T, C, F>(this) {
            @Override
            protected F getFilter(Query<T, C> query) {
                return FilterUtils.convertFilter(filterConverter, query);
            }
        };
    }

    @Override
    default HierarchicalConfigurableFilterDataProvider<T, Void, F> withConfigurableFilter() {
        return (HierarchicalConfigurableFilterDataProvider<T, Void, F>) DataProvider.super.withConfigurableFilter();
    }
}

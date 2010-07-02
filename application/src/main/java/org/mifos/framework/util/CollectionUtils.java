/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.util;

import org.mifos.framework.util.helpers.Predicate;
import org.mifos.framework.util.helpers.Transformer;

import java.util.*;

public class CollectionUtils {

    // Same as commons-collections but with generics
    public static <T> T find(Collection<T> collections, Predicate<T> predicate) throws Exception {
        if (collections != null && predicate != null) {
            for (T item : collections) {
                if (predicate.evaluate(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    public static <T> boolean exists(Collection<T> collections, Predicate<T> predicate) throws Exception {
        return null != find(collections, predicate);
    }

    public static <T> List<T> asList(T... elements) {
        List<T> list = new ArrayList<T>();
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }

    public static <T> Set<T> asSet(T element) {
        Set<T> collectionSheetsForMeetingDate = new HashSet<T>();
        collectionSheetsForMeetingDate.add(element);
        return collectionSheetsForMeetingDate;
    }

    public static <T> T first(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        return collection.iterator().next();
    }

    public static <T> T last(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        Iterator<T> iterator = collection.iterator();
        T elem = null;
        while (iterator.hasNext()) {
            elem = iterator.next();
        }
        return elem;
    }

    public static <T> T last(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public static <T> Collection<T> select(Collection<T> collection, Predicate<T> predicate) throws Exception {
        Collection<T> outputCollection = new ArrayList<T>();
        select(collection, predicate, outputCollection);
        return outputCollection;
    }

    public static <T, O> Collection<O> select(Collection<T> collection, Predicate<T> predicate,
            Transformer<T, O> transformer) throws Exception {
        Collection<T> selectedCollection = select(collection, predicate);
        return collect(selectedCollection, transformer);
    }

    public static <T, O> Collection<O> collect(Collection<T> collection, Transformer<T, O> transformer) {
        Collection<O> outputCollection = new ArrayList<O>();
        for (T item : collection) {
            outputCollection.add(transformer.transform(item));
        }
        return outputCollection;
    }

    public static <T> void select(Collection<T> collection, Predicate<T> predicate, Collection<T> outputCollection)
            throws Exception {
        if (collection != null && predicate != null) {
            for (T item : collection) {
                if (predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
    }

    public static List<List> splitListIntoParts(List list, int sizeOfEachPart) {
        if (sizeOfEachPart <= 0) {
            throw new IllegalArgumentException("Cannot split list into sizes of zero or less. Given value: "
                    + sizeOfEachPart);
        }

        List<List> result = new ArrayList<List>();
        int start = 0, end = sizeOfEachPart;
        while (start < list.size()) {
            result.add(list.subList(start, Math.min(end, list.size())));
            start = end;
            end += sizeOfEachPart;
        }
        return result;
    }

    public static <K, V> Map<K, V> asOrderedMap(Map.Entry<K, V>... entry) {
        Map<K, V> map = new LinkedHashMap<K, V>();
        fillEntries(map, entry);
        return map;
    }

    public static <K, V> Map<K, V> asMap(Map.Entry<K, V>... entry) {
        Map<K, V> map = new HashMap<K, V>();
        fillEntries(map, entry);
        return map;
    }

    private static <K, V> void fillEntries(Map<K, V> map, Map.Entry<K, V>... entry) {
        for (Map.Entry<K, V> _entry : entry) {
            map.put(_entry.getKey(), _entry.getValue());
        }
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
}

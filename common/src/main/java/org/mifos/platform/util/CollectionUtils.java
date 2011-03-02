/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.platform.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.EMPTY;

public class CollectionUtils {
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

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static <T> String toString(Collection<T> collection) {
        String result = EMPTY;
        if (isNotEmpty(collection)) {
            String collStr = collection.toString();
            result = collStr.substring(1, collStr.length() - 1);
        }
        return result;
    }

    public static <K, V> void addKeyValue(Map<K, List<V>> multiMap, K key, V value) {
        if (key != null) {
            List<V> values;
            if (multiMap.containsKey(key)) {
                values = multiMap.get(key);
            } else {
                values = new ArrayList<V>();
                multiMap.put(key, values);
            }
            values.add(value);
        }
    }

    public static <T, O> List<O> collect(List<T> listOfItems, Transformer<T, O> transformer) {
        List<O> outputCollection = new ArrayList<O>();
        for (T item : listOfItems) {
            outputCollection.add(transformer.transform(item));
        }
        return outputCollection;
    }

    public static <T extends Comparable<T>> int itemIndexOutOfAscendingOrder(List<T> listOfItems) {
        int resultIndex = -1;
        for (int i = 1, collectionSize = listOfItems.size(); i < collectionSize; i++) {
            T previousItem = listOfItems.get(i - 1);
            T currentItem = listOfItems.get(i);
            if (!isSecondValueGreaterThanFirstValue(previousItem, currentItem)) {
                resultIndex = i;
                break;
            }
        }
        return resultIndex;
    }
    
    public static <T extends Comparable<T>> boolean isSecondValueGreaterThanFirstValue(T first, T second) {
        return first == null || second != null && second.compareTo(first) > 0;
    }
}


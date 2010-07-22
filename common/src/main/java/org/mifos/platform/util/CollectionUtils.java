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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
}

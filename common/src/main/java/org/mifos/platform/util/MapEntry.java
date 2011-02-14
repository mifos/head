/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.util.Map;

public final class MapEntry<K, V> implements Map.Entry<K,V> {
    private final K key;
    private V value;

    private MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> MapEntry<K, V> makeEntry(K k, V v) {
        return new MapEntry<K,V>(k, v);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return this.value;
    }

    @Override
    @SuppressWarnings({"PMD.OnlyOneReturn", "PMD.ConfusingTernary"})
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MapEntry mapEntry = (MapEntry) o;

        if (key != null ? !key.equals(mapEntry.key) : mapEntry.key != null) {
            return false;
        }
        if (value != null ? !value.equals(mapEntry.value) : mapEntry.value != null) {
            return false;
        }

        return true;
    }

    @Override
    @SuppressWarnings({"PMD.OnlyOneReturn", "PMD.ConfusingTernary"})
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}

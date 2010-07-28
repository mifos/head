package org.mifos.platform.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;
import static java.util.Collections.list;


public class CollectionUtilsTest extends TestCase {

    public void testAsMap() {
        Map<Integer, String> map = CollectionUtils.asMap(MapEntry.makeEntry(1, "One"), MapEntry.makeEntry(2, "Two"), MapEntry.makeEntry(3, "Three"));
        assertTrue(map.containsKey(1) && map.containsValue("One"));
        assertTrue(map.containsKey(2) && map.containsValue("Two"));
        assertTrue(map.containsKey(3) && map.containsValue("Three"));
    }

    public void testAsOrderedMap() {
        Map<Integer, String> map = CollectionUtils.asOrderedMap(MapEntry.makeEntry(1, "1"), MapEntry.makeEntry(2, "2"), MapEntry.makeEntry(3, "3"));
        ArrayList<Map.Entry<Integer, String>> entryList = list(enumeration(map.entrySet()));
        for (int i=1; i<=entryList.size(); i++) {
            Map.Entry<Integer, String> entry = entryList.get(i - 1);
            assertEquals(Integer.valueOf(i), entry.getKey());
            assertEquals(String.valueOf(i), entry.getValue());
        }
    }

    public void testCollectionEmpty() {
        assertEquals(false, CollectionUtils.isEmpty(asList("Hi", "Bye")));
        assertEquals(true, CollectionUtils.isEmpty(Collections.EMPTY_SET));
        assertEquals(true, CollectionUtils.isEmpty(null));
    }

    public void testCollectionNotEmpty() {
        assertEquals(true, CollectionUtils.isNotEmpty(asList("Hi", "Bye")));
        assertEquals(false, CollectionUtils.isNotEmpty(Collections.EMPTY_SET));
        assertEquals(false, CollectionUtils.isNotEmpty(null));
    }
    
    public void testCollectionToString() {
        assertEquals(null, CollectionUtils.toString(null));
        assertEquals(null, CollectionUtils.toString(Collections.EMPTY_LIST));
        assertEquals("Hi", CollectionUtils.toString(asList("Hi")));
        assertEquals("Hi, Bye", CollectionUtils.toString(asList("Hi", "Bye")));
    }
}

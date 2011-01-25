package org.mifos.platform.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.platform.util.CollectionUtils.asMap;
import static org.mifos.platform.util.MapEntry.makeEntry;


public class CollectionUtilsTest extends TestCase {

    public void testAsMap() {
        Map<Integer, String> map = asMap(makeEntry(1, "One"), makeEntry(2, "Two"), makeEntry(3, "Three"));
        assertTrue(map.containsKey(1) && map.containsValue("One"));
        assertTrue(map.containsKey(2) && map.containsValue("Two"));
        assertTrue(map.containsKey(3) && map.containsValue("Three"));
    }

    public void testAsOrderedMap() {
        Map<Integer, String> map = CollectionUtils.asOrderedMap(makeEntry(1, "1"), makeEntry(2, "2"), makeEntry(3, "3"));
        ArrayList<Map.Entry<Integer, String>> entryList = Collections.list(Collections.enumeration(map.entrySet()));
        for (int i=1; i<=entryList.size(); i++) {
            Map.Entry<Integer, String> entry = entryList.get(i - 1);
            assertEquals(Integer.valueOf(i), entry.getKey());
            assertEquals(String.valueOf(i), entry.getValue());
        }
    }

    public void testCollectionEmpty() {
        assertEquals(false, CollectionUtils.isEmpty(Arrays.asList("Hi", "Bye", "One")));
        assertEquals(true, CollectionUtils.isEmpty(Collections.EMPTY_SET));
        assertEquals(true, CollectionUtils.isEmpty(null));
    }

    public void testCollectionNotEmpty() {
        assertEquals(true, CollectionUtils.isNotEmpty(Arrays.asList("Hi", "Bye", "One")));
        assertEquals(false, CollectionUtils.isNotEmpty(Collections.EMPTY_SET));
        assertEquals(false, CollectionUtils.isNotEmpty(null));
    }

    public void testCollectionToString() {
        assertEquals("", CollectionUtils.toString(null));
        assertEquals("", CollectionUtils.toString(Collections.EMPTY_LIST));
        assertEquals("Hi", CollectionUtils.toString(Arrays.asList("Hi")));
        assertEquals("Hi, Bye", CollectionUtils.toString(Arrays.asList("Hi", "Bye")));
    }
    public void testAddKeyValue() {
        Map<Integer, List<String>> multiMap = asMap(
                            makeEntry(1, asStringList("One")),
                            makeEntry(2, asStringList("Two")),
                            makeEntry(3, asStringList("Three")));

        CollectionUtils.addKeyValue(multiMap, 2, "Deuce");
        CollectionUtils.addKeyValue(multiMap, 2, "Double");
        CollectionUtils.addKeyValue(multiMap, 3, "Triple");
        assertThat(multiMap.get(1).get(0),is("One"));
        assertThat(multiMap.get(2).get(0),is("Two"));
        assertThat(multiMap.get(2).get(1),is("Deuce"));
        assertThat(multiMap.get(2).get(2),is("Double"));
        assertThat(multiMap.get(3).get(0),is("Three"));
        assertThat(multiMap.get(3).get(1),is("Triple"));
    }

    private List<String> asStringList(String str) {
        ArrayList<String> strings = new ArrayList<String>();
        strings.add(str);
        return strings;
    }
}

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

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.*;

import static java.util.Collections.enumeration;
import static java.util.Collections.list;
import static org.mifos.framework.util.CollectionUtils.*;
import static org.mifos.framework.util.MapEntry.makeEntry;

public class CollectionUtilsTest extends TestCase {

    public void testAsListReturnsOneElementPassed() {
        List<Integer> list = asList(Integer.valueOf(0));
       assertEquals(1, list.size());
       assertEquals(Integer.valueOf(0), list.get(0));
    }

    public void testAsListReturnsListFormedOfMultipleElements() throws Exception {
        List<Integer> list = asList(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
       assertEquals(3, list.size());
       assertEquals(Integer.valueOf(0), list.get(0));
       assertEquals(Integer.valueOf(1), list.get(1));
       assertEquals(Integer.valueOf(2), list.get(2));
    }

    public void testSplitListReturnsEmptyListForEmptyList() throws Exception {
       assertEquals(Collections.EMPTY_LIST, splitListIntoParts(new ArrayList(), 10));
    }

    public void testSplitListThrowsExceptionIfSizeOfPartsIsZero() throws Exception {
        try {
            splitListIntoParts(Arrays.asList(1, 2, 3), 0);
            Assert.fail("Split list should throw exception if size of each part is zero");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testSplitReturnsSameListIfSizeOfEachPartIsGreaterThanListSize() throws Exception {
        List expected = new ArrayList();
        expected.add(Arrays.asList(1, 2, 3));
       assertEquals(expected, splitListIntoParts(Arrays.asList(1, 2, 3), 4));
    }

    public void testSplitListEvenlyIfListSizeIsMultipleOfSizeOfEachPart() throws Exception {
        List expected = new ArrayList();
        expected.add(Arrays.asList(1, 2));
        expected.add(Arrays.asList(3, 4));
       assertEquals(expected, splitListIntoParts(Arrays.asList(1, 2, 3, 4), 2));
    }

    public void testSplitReturnsOneSublistIfListSizeEqualsSizeOfEachPart() throws Exception {
        List expected = new ArrayList();
        expected.add(Arrays.asList(1, 2, 3));
       assertEquals(expected, splitListIntoParts(Arrays.asList(1, 2, 3), 3));
    }

    public void testSplitListIntoPartsAndARemainderIfListSizeIsNotMultipleOfSizeOfEachPart() throws Exception {
        List expected = new ArrayList();
        expected.add(Arrays.asList(1, 2));
        expected.add(Arrays.asList(3, 4));
        expected.add(Arrays.asList(5));
       assertEquals(expected, splitListIntoParts(Arrays.asList(1, 2, 3, 4, 5), 2));
    }

    public void testAsMap() {
        Map<Integer, String> map = asMap(makeEntry(1, "One"), makeEntry(2, "Two"), makeEntry(3, "Three"));
        Assert.assertTrue(map.containsKey(1) && map.containsValue("One"));
        Assert.assertTrue(map.containsKey(2) && map.containsValue("Two"));
        Assert.assertTrue(map.containsKey(3) && map.containsValue("Three"));
    }

    public void testAsOrderedMap() {
        Map<Integer, String> map = asOrderedMap(makeEntry(1, "1"), makeEntry(2, "2"), makeEntry(3, "3"));
        ArrayList<Map.Entry<Integer, String>> entryList = list(enumeration(map.entrySet()));
        for (int i=1; i<=entryList.size(); i++) {
            Map.Entry<Integer, String> entry = entryList.get(i - 1);
            assertEquals(Integer.valueOf(i), entry.getKey());
            assertEquals(String.valueOf(i), entry.getValue());
        }
    }
    
    public void testCollectionEmpty() {
        assertEquals(false, isEmpty(Arrays.asList("Hi", "Bye")));
        assertEquals(true, isEmpty(Collections.EMPTY_SET));
        assertEquals(true, isEmpty(null));
    }
}

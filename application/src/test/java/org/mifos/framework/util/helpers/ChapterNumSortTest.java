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

package org.mifos.framework.util.helpers;

import static org.junit.Assert.assertArrayEquals;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.testng.annotations.Test;

/**
 * Demonstrate sorting number-like strings such as 1.4.2 and 1.4.10, where 1.4.2
 * would come before 1.4.10 in the sequence.
 */
@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class ChapterNumSortTest extends TestCase {

    public void testChapterNumberOrder() {
        String[] dottedNums = { "1.4.10", "2", "1.4", "1.15.3", "1.4", "0.1", "1.4.2" };
        String[] expected = { "0.1", "1.4", "1.4", "1.4.2", "1.4.10", "1.15.3", "2" };
        String[] actual = ChapterNumSorter.sortChapterNumbers(dottedNums);
        assertArrayEquals(expected, actual);
    }

    public void testChapterNumberOrderWithGarbage() {
        String[] dottedNums = { "1", "3", "", "blah", "2.1", "-3" };
        String[] expected = { "1", "2.1", "3" };
        String[] actual = ChapterNumSorter.sortChapterNumbers(dottedNums);
        assertArrayEquals(expected, actual);
    }

    public void testSimpleCompare() {
       Assert.assertEquals(1, ChapterNum.fromString("1.4.1").compareTo(ChapterNum.fromString("1.4.0")));
       Assert.assertEquals(-1, ChapterNum.fromString("1.4.1").compareTo(ChapterNum.fromString("1.4.10")));
    }
}

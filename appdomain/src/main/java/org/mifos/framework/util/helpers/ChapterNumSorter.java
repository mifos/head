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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides sorting routines for sorting strings that look like dotted chapter
 * numbers, like "1.2.15" and "6.3.28".
 */
public class ChapterNumSorter {

    /**
     * Sorts a list of strings contained dotted numeric tuples. For example, the
     * following input list of {@link String}s:
     * <ul>
     * <li>1.4.10</li>
     * <li>2</li>
     * <li>1.4</li>
     * <li>1.15.3</li>
     * <li>1.4</li>
     * <li>0.1</li>
     * <li>1.4.2</li>
     * </ul>
     * Produces this list of output {@link String}s:
     * <ul>
     * <li>0.1</li>
     * <li>1.4</li>
     * <li>1.4</li>
     * <li>1.4.2</li>
     * <li>1.4.10</li>
     * <li>1.15.3</li>
     * <li>2</li>
     * </ul>
     *
     * <p>
     * {@link String}s are parsed one at a time by
     * {@link ChapterNum#fromString(String)}.
     */
    public static String[] sortChapterNumbers(String[] unsorted) {
        String[] sorted = null;
        List<ChapterNum> allChaps = new ArrayList<ChapterNum>();

        // transform each string into a list of ints.
        // "1.4.1" becomes [1, 4, 1].
        for (String s : unsorted) {
            ChapterNum chapterNum = ChapterNum.fromString(s);
            if (null != chapterNum) {
                allChaps.add(chapterNum);
            }
        }

        Collections.sort(allChaps);

        List<String> orderedDottedNumbers = new ArrayList<String>();
        for (ChapterNum t : allChaps) {
            orderedDottedNumbers.add(t.toString());
        }

        sorted = orderedDottedNumbers.toArray(new String[0]);

        // if we didn't find anything worthwile, return an empty array rather
        // than a null
        if (null == sorted) {
            sorted = new String[0];
        }

        return sorted;
    }
}

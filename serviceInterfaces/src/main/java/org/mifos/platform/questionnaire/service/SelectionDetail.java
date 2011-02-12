/*
 * Copyright Grameen Foundation USA
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

package org.mifos.platform.questionnaire.service;

import java.io.Serializable;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.split;

public class SelectionDetail implements Serializable {
    private static final long serialVersionUID = 6631044340750607074L;

    private String selectedChoice;
    private String selectedTag;
    private static final char CHOICE_TAG_SEPARATOR = ':';

    @SuppressWarnings("PMD.UncommentedEmptyConstructor")
    public SelectionDetail() {
    }

    @SuppressWarnings("PMD.NullAssignment")
    public SelectionDetail(String choiceTagString) {
        String[] values = split(choiceTagString, CHOICE_TAG_SEPARATOR);
        if (values != null && values.length > 0) {
            selectedChoice = values[0];
            selectedTag = (values.length > 1)? values[1]: null;
        }
    }

    public String getSelectedChoice() {
        return selectedChoice;
    }

    public void setSelectedChoice(String selectedChoice) {
        this.selectedChoice = selectedChoice;
    }

    public String getSelectedTag() {
        return selectedTag;
    }

    public void setSelectedTag(String selectedTag) {
        this.selectedTag = selectedTag;
    }

    @Override
    public String toString() {
        return isEmpty(selectedTag)? selectedChoice: format("%s%s%s", selectedChoice, CHOICE_TAG_SEPARATOR, selectedTag);
    }
}

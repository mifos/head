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

package org.mifos.platform.questionnaire.service;

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChoiceDetail implements Serializable {
    private static final long serialVersionUID = 5839636913158754732L;

    private String value;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<String> tags;
    private Integer order;

    public ChoiceDetail() {
        this(null);
    }

    public ChoiceDetail(String value) {
        this.value = value;
        tags = new ArrayList<String>();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getCommaSeparatedTags() {
        return CollectionUtils.toString(this.tags);
    }

    @Override
    public String toString() {
        return value;
    }

    public void addTag(String tag) {
        if (tags.size() < 5 && !isDuplicateTag(tag)) {
            tags.add(tag);
        }
    }

    private boolean isDuplicateTag(String tag) {
        boolean result = false;
        for (String _tag : tags) {
            if (StringUtils.equalsIgnoreCase(_tag, tag)) {
                result = true;
            }
        }
        return result;
    }

    public void removeTag(int tagIndex) {
        tags.remove(tagIndex);
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}

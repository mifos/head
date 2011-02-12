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

package org.mifos.platform.questionnaire.service.dtos;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.lang.StringUtils;
import org.mifos.platform.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.trim;

@XStreamAlias("choice")
public class ChoiceDto implements Serializable {
    private static final long serialVersionUID = 5839636913158754732L;

    private String value;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    @XStreamImplicit(itemFieldName = "tag")
    private List<String> tags;
    @XStreamAsAttribute
    private Integer order;

    public ChoiceDto() {
        this(null);
    }

    public ChoiceDto(String value) {
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
        if (!isTagsLimitReached() && !isDuplicateTag(tag)) {
            tags.add(tag);
        }
    }

    public boolean isTagsLimitReached() {
        return tags.size() >= 5;
    }

    public boolean isDuplicateTag(String tag) {
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

    public void trimValue() {
        this.value = trim(this.value);
    }
}

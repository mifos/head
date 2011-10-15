/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.application.messagecustomizer;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.ejb.QueryHints;

@NamedQueries({
        @NamedQuery(name = "allMessages", query = "from CustomizedText"),
        @NamedQuery(name = "findCustomMessageByOldMessage",
                    query = "from CustomizedText message where message.originalText=:originalText") })
@NamedNativeQuery(name = "allMessagesNative",
                  query = "select * from customized_text order by char_length(original_text) desc",
                  hints = { @QueryHint(name=QueryHints.HINT_CACHEABLE,value="true") },
                  resultClass = CustomizedText.class)
@Entity
@Table(name = "customized_text")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CustomizedText {

    @Id
    private String originalText;
    private String customText;

    protected CustomizedText() {
    }

    public CustomizedText(String originalText, String customText) {
        super();
        this.originalText = originalText;
        this.customText = customText;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getCustomText() {
        return customText;
    }

    public void setCustomText(String customText) {
        this.customText = customText;
    }

}

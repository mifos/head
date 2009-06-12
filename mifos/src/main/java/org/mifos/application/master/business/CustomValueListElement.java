/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.master.business;

import java.io.Serializable;

import org.mifos.application.master.MessageLookup;
import org.mifos.framework.business.View;

/**
 * It is unclear why this class extends View. Note that the class
 * {@link BusinessActivityEntity} serves the same purpose but doesn't include
 * the additional id. A CustomValueListElement represents a
 * {@link CustomValueList} element for a given locale. This corresponds to a
 * {@link LookUpValueEntity} with the text pulled in from a
 * {@link LookUpValueLocaleEntity} for a given locale.
 * 
 * A better name for this class might be ValueListElementForLocale.
 */
public class CustomValueListElement extends View implements Serializable {

    /**
     * The id of an associated object linked to this list element. This only
     * appears to be used with {@link CustomerAttendence} for use related to
     * bulk entry.
     */
    private Integer associatedId;

    /**
     * The id of the {@link LookUpValueEntity} corresponding to this value list
     * element
     */
    private Integer lookUpId;

    /**
     * The text value of this list element for a given locale (from the
     * {@link LookUpValueLocaleEntity})
     */
    private String lookUpValue;

    /**
     * The text value of the key from {@link LookUpValueEntity} for looking up
     * text in a given locale.)
     */
    private String lookUpValueKey;

    public CustomValueListElement() {
    }

    public CustomValueListElement(java.lang.Integer lookUpId, String lookUpValue, String lookUpValueKey) {

        this.lookUpId = lookUpId;
        this.lookUpValue = lookUpValue;
        this.lookUpValueKey = lookUpValueKey;
    }

    /**
     * Possibly used in MasterPersistence.getCustomValueListElements
     */
    public CustomValueListElement(java.lang.Short id, java.lang.Integer lookUpId, String lookUpValue,
            String lookUpValueKey) {

        this.lookUpId = lookUpId;
        this.lookUpValue = lookUpValue;
        this.associatedId = id.intValue();
        this.lookUpValueKey = lookUpValueKey;
    }

    /**
     * Possibly used in MasterPersistence.getCustomValueListElements
     */
    public CustomValueListElement(java.lang.Integer id, java.lang.Integer lookUpId, String lookUpValue) {

        this.lookUpId = lookUpId;
        this.lookUpValue = lookUpValue;
        this.associatedId = id;
    }

    /**
     * This method is used in some places, but it is unclear if the id value is
     * ever set.
     */
    public java.lang.Integer getAssociatedId() {
        return associatedId;
    }

    public java.lang.Integer getLookUpId() {
        return lookUpId;
    }

    /*
     * Use the key for the LookUpValueEntity to resolve the value.
     */
    public java.lang.String getLookUpValue() {
        return MessageLookup.getInstance().lookup(lookUpValueKey);
    }

    public void setLookupValue(String newValue) {
        lookUpValue = newValue;
    }

    public String getName() {
        return getLookUpValue();
    }

    public Integer getId() {
        return lookUpId;
    }

    public void setId(Integer id) {
        lookUpId = id;
    }

    public String getLookUpValueKey() {
        return lookUpValueKey;
    }

    public void setLookUpValueKey(String lookUpValueKey) {
        this.lookUpValueKey = lookUpValueKey;
    }
}

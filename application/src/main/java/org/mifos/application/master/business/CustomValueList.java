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

package org.mifos.application.master.business;

import java.util.List;

import org.mifos.framework.business.View;

/**
 * This class represents a {@link LookUpEntity} and its associated list of
 * {@link LookUpValueEntity} objects for a given locale.
 *
 * A better name for this class might be ValueListForLocale.
 */
public class CustomValueList extends View {

    private Short entityId;

    private Short localeId;

    private String entityLabel;

    private List<CustomValueListElement> customValueListElements;

    public CustomValueList() {
    }

    /**
     * This is only used in the HQL query "masterdata.entityvalue" in
     * LookUpEntity
     */
    public CustomValueList(Short entityId, Short localeId, String entityLabel) {

        this.entityId = entityId;
        this.localeId = localeId;
        this.entityLabel = entityLabel;
    }

    public java.lang.Short getEntityId() {
        return entityId;
    }

    public java.lang.Short getlocaleId() {
        return localeId;
    }

    public java.lang.String getEntityLabel() {
        return entityLabel;
    }

    /**
     * Method which returns the customValueListElements
     *
     * @return Returns the customValueListElements.
     */
    public List<CustomValueListElement> getCustomValueListElements() {
        return customValueListElements;
    }

    /**
     * Method which returns an array of the customValueListElement Strings
     */
    public String[] getCustomValueListElementsAsStrings() {
        String stringArray[] = new String[customValueListElements.size()];
        int elementIndex = 0;
        for (CustomValueListElement element : customValueListElements) {
            stringArray[elementIndex++] = element.getLookUpValue();
        }
        ;
        return stringArray;
    }

    /**
     * Method which sets the customValueListElements
     *
     * @param customValueListElements
     *            The customValueListElements to set.
     */
    public void setCustomValueListElements(List<CustomValueListElement> customValueListElements) {
        this.customValueListElements = customValueListElements;
    }

    /**
     * Method which obtains a particular lookupValue for the given lookup id
     *
     * @param lookUpId the lookup id.
     */
    public String getLookUpValueForId(int lookUpId) {
        String lookUpValue = "";
        for (int i = 0; i < customValueListElements.size(); i++) {
            CustomValueListElement lookUpEntityValue = customValueListElements.get(i);
            if (lookUpId == lookUpEntityValue.getLookUpId().intValue()) {
                lookUpValue = lookUpEntityValue.getLookUpValue();
                break;
            }
        }
        return lookUpValue;
    }

}

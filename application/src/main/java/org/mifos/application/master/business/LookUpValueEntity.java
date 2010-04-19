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

import java.util.Set;

import org.mifos.application.master.MessageLookup;
import org.mifos.config.LocalizedTextLookup;
import org.mifos.framework.business.AbstractEntity;

public class LookUpValueEntity extends AbstractEntity implements LocalizedTextLookup {

    private Integer lookUpId;

    /*
     * The key used for retrieving localized resource bundle message text.
     */
    private String lookUpName;

    private LookUpEntity lookUpEntity;

    private Set<LookUpValueLocaleEntity> lookUpValueLocales;

    public String getLookUpName() {
        return lookUpName;
    }

    public void setLookUpName(String lookUpName) {
        this.lookUpName = lookUpName;
    }

    public Integer getLookUpId() {
        return lookUpId;
    }

    public void setLookUpId(Integer lookUpId) {
        this.lookUpId = lookUpId;
    }

    public LookUpEntity getLookUpEntity() {
        return lookUpEntity;
    }

    public void setLookUpEntity(LookUpEntity lookUpEntity) {
        this.lookUpEntity = lookUpEntity;
    }

    public Set<LookUpValueLocaleEntity> getLookUpValueLocales() {
        return lookUpValueLocales;
    }

    public void setLookUpValueLocales(Set<LookUpValueLocaleEntity> lookUpValueLocales) {
        this.lookUpValueLocales = lookUpValueLocales;
    }

    /*
     * Get the localized text for this object (or the override value from the
     * database if present)
     */
    public String getMessageText() {
        String messageText = null;
        Set<LookUpValueLocaleEntity> list = getLookUpValueLocales();
        if (list != null) {
            for (LookUpValueLocaleEntity lookUpValueLocale : list) {
                if (lookUpValueLocale.getLocaleId().equals(MasterDataEntity.CUSTOMIZATION_LOCALE_ID)) {
                    messageText = lookUpValueLocale.getLookUpValue();
                }
            }
        }

        if (messageText != null && messageText.length() > 0) {
            return messageText;
        }

        return MessageLookup.getInstance().lookup(this);
    }

    /*
     * The key used to lookup localized properties text.
     */
    public String getPropertiesKey() {
        return getLookUpName();
    }

}

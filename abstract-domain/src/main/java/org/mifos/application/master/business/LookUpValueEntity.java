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

package org.mifos.application.master.business;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifos.config.LocalizedTextLookup;
import org.mifos.framework.business.AbstractEntity;

@NamedQueries({
    @NamedQuery(name = "lookupvalues", query = "from LookUpValueEntity")
})

@Entity
@Table(name = "lookup_value")
public class LookUpValueEntity extends AbstractEntity implements LocalizedTextLookup {

    @Id
    @GeneratedValue
    @Column(name = "lookup_id", nullable = false)
    private Integer lookUpId;

    /*
     * The key used for retrieving localized resource bundle message text.
     */
    @Column(name = "lookup_name")
    private String lookUpName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", unique = true)
    private LookUpEntity lookUpEntity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "lookup_id", updatable = false)
    private Set<LookUpValueLocaleEntity> lookUpValueLocales;

    public LookUpValueEntity(){
    }
    public LookUpValueEntity(Integer lookUpId, String lookUpName){
        this.lookUpId = lookUpId;
        this.lookUpName = lookUpName;
    }

    public LookUpValueEntity(String lookUpName){
        this.lookUpName = lookUpName;
    }

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
                if (lookUpValueLocale.getLocaleId().equals((short) 1)) {
                    messageText = lookUpValueLocale.getLookUpValue();
                }
            }
        }

        if (messageText != null && messageText.length() > 0) {
            return messageText;
        }
     // ApplicationContextProvider.getBean(MessageLookup.class).lookup(entity.getPropertiesKey();)
        return "";
    }

    /*
     * The key used to lookup localized properties text.
     */
    @Override
	public String getPropertiesKey() {
        return getLookUpName();
    }

    public void update(String newValue) {
        Set<LookUpValueLocaleEntity> lookUpValueLocales = getLookUpValueLocales();
        if ((lookUpValueLocales != null) && StringUtils.isNotBlank(newValue)) {
            for (LookUpValueLocaleEntity entity : lookUpValueLocales) {
                if (entity.getLookUpId().equals(getLookUpId())
                        && (entity.getLookUpValue() == null || !entity.getLookUpValue().equals(newValue))) {
                    entity.setLookUpValue(newValue);
                    break;
                }
            }
        }

    }
}

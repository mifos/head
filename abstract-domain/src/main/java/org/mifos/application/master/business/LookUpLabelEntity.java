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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.mifos.framework.business.AbstractEntity;

@Entity
@Table(name = "lookup_label")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class LookUpLabelEntity extends AbstractEntity {

    @Id
    @GeneratedValue
    @Column(name = "label_id", nullable = false)
    private Short LookUpLabelId;

    @Column(name = "entity_name")
    private String labelName;

    @Column(name = "locale_id")
    private Short localeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id")
    private LookUpEntity lookUpEntity;

    public String getLabelKey() {
        return lookUpEntity.getEntityType();
    }

    public String getLabelText() {
        if (labelName != null && labelName.length() > 0) {
            return labelName;
        }

        // if we don't find a label here, then it means that it has not been
        // customized and
        // we should return the default label from the properties file

        // note: instead of message lookup here, we use it further out and use labelkey
        return "";
    }

    /*
     * This method is only for use by Hibernate to persist this class. To get
     * the label text back use {@link getLabelText()}
     */
    protected String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public void setLocaleId(Short localeId) {
        this.localeId = localeId;
    }

    public Short getLocaleId() {
        return localeId;
    }

    public Short getLookUpLabelId() {
        return LookUpLabelId;
    }

    public void setLookUpLabelId(Short lookUpLabelId) {
        LookUpLabelId = lookUpLabelId;
    }

    public LookUpEntity getLookUpEntity() {
        return lookUpEntity;
    }

    public void setLookUpEntity(LookUpEntity lookUpEntity) {
        this.lookUpEntity = lookUpEntity;
    }

}

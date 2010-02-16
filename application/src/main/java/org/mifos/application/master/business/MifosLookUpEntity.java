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
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.mifos.framework.business.PersistentObject;

/**
 * {@link MifosLookUpEntity} and {@link LookUpEntity} were redundant classes.
 * {@link LookUpEntity} usage has now been replaced by this class.
 * 
 * The entityType field should be a CamelCase name containing no whitespace
 * (since it is used as part of a properties file key value) The no whitespace
 * requirement is enforced by the unit test
 * ApplicationConfigurationPersistenceIntegrationTest.testGetLookupEntities()
 */
@Entity
@Table(name = "LOOKUP_ENTITY")
@NamedQueries(
 {         
  @NamedQuery(
    name="entities",
    query="from MifosLookUpEntity "
  ),
  @NamedQuery(
    name="masterdata.mifosEntityValue",
    query="select new org.mifos.application.master.business.BusinessActivityEntity(value.lookUpId ,value.lookUpName, value.lookUpName) "+
          "from MifosLookUpEntity entity, LookUpValueEntity value "+
          "where entity.entityId = value.lookUpEntity.entityId and entity.entityType=:entityType " 
  ) 
 }
)
public class MifosLookUpEntity implements Serializable {

    public static final Short DEFAULT_LOCALE_ID = 1;

    public static final int ETHNICITY = 19;
    public static final int ACCOUNT_ACTION = 69;
    public static final int ACCOUNT_STATE_FLAG = 70;
    public static final int ACTIVITY = 87;
    public static final int REPAYMENT_RULE = 91;
    public static final int INTEREST_TYPES = 37;
    public static final int FINANCIAL_ACTION = 76;

    private Short entityId;

    private String entityType;

    private Set<LookUpLabelEntity> lookUpLabels;

    private Set<LookUpValueEntity> lookUpValues;

    public MifosLookUpEntity() {
        super();
    }

    @Id
    @GeneratedValue
    @Column(name = "ENTITY_ID", nullable = false)
    public Short getEntityId() {
        return entityId;
    }

    public void setEntityId(Short entityId) {
        this.entityId = entityId;
    }

    @Column(name = "ENTITY_NAME")
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_ID", updatable = false)
    public Set<LookUpLabelEntity> getLookUpLabels() {
        return lookUpLabels;
    }

    public void setLookUpLabels(Set<LookUpLabelEntity> lookUpLabels) {
        this.lookUpLabels = lookUpLabels;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_ID", updatable = false)
    public Set<LookUpValueEntity> getLookUpValues() {
        return lookUpValues;
    }

    public void setLookUpValues(Set<LookUpValueEntity> lookUpValues) {
        this.lookUpValues = lookUpValues;
    }

    public String findLabel() {
        return findLabelForLocale(DEFAULT_LOCALE_ID);
    }

    private String findLabelForLocale(Short localeId) {
        for (LookUpLabelEntity lookUpLabel : lookUpLabels) {
            if (lookUpLabel.getLocaleId().equals(localeId)) {
                return lookUpLabel.getLabelText();
            }
        }
        throw new RuntimeException("Label not found for locale with id: \"" + localeId + "\"");
    }

}

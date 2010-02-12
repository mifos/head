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

/**
 * {@link LookUpEntity} is now deprecated and should not be used. Use
 * {@link MifosLookUpEntity} instead which provides the same functionality and
 * includes the constants previously defined here.
 * 
 * This class can probably be deleted now-- will make sure no issues show up
 * first.
 */
@Entity
@Table(name = "LOOKUP_ENTITY")
@NamedQueries(
 {         
  @NamedQuery(
    name="masterdata.entityvalue",
    query="select new org.mifos.application.master.business.CustomValueList(entity.entityId ,label.localeId,label.labelName) "
                +"from org.mifos.application.master.business.LookUpEntity entity, "
                +"org.mifos.application.master.business.LookUpLabelEntity label "
                +"where entity.entityId = label.lookUpEntity.entityId "
                +"and entity.entityType=:entityType"
  ),
  @NamedQuery(
    name="masterdata.entitylookupvalue",
    query="select new org.mifos.application.master.business.CustomValueListElement("
                +"lookup.lookUpId,lookup.lookUpName, lookup.lookUpName) "
                +"from org.mifos.application.master.business.LookUpValueEntity lookup,"
                +"org.mifos.application.master.business.LookUpEntity entity "
                +"where entity.entityType=:entityType and lookup.lookUpEntity.entityId =entity.entityId" 
  ) 
 }
)
public class LookUpEntity implements Serializable {

    private Short entityId;

    private String entityType;

    private Set<LookUpLabelEntity> lookUpLabelSet;

    private Set<LookUpValueEntity> lookUpValueSet;

    public LookUpEntity() {
        super();
    }
    
    @Id
    @GeneratedValue
    @Column(name = "ENTITY_ID", nullable = false)
    public Short getEntityId() {
        return entityId;
    }

    @Column(name = "ENTITY_NAME")
    public String getEntityType() {
        return entityType;
    }
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_ID", updatable = false)
    public Set<LookUpLabelEntity> getLookUpLabelSet() {
        return lookUpLabelSet;
    }
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_ID", updatable = false)
    public Set<LookUpValueEntity> getLookUpValueSet() {
        return lookUpValueSet;
    }

    public void setEntityId(Short entityId) {
        this.entityId = entityId;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setLookUpLabelSet(Set<LookUpLabelEntity> lookUpLabelSet) {
        this.lookUpLabelSet = lookUpLabelSet;
    }

    public void setLookUpValueSet(Set<LookUpValueEntity> lookUpValueSet) {
        this.lookUpValueSet = lookUpValueSet;
    }

}

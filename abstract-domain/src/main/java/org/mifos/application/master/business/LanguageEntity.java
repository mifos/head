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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.ejb.QueryHints;

@NamedQueries({
    @NamedQuery(name = "availableLanguages", query = "from  LanguageEntity",
                hints = { @QueryHint(name=QueryHints.HINT_CACHEABLE,value="true") })
})

@Entity
@Table(name = "language")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class LanguageEntity extends MasterDataEntity {

    @Id
    @GeneratedValue
    @Column(name = "lang_id", nullable = false)
    private Short id;

    @Column(name = "lang_name")
    private final String languageName;

    @Column(name = "lang_short_name")
    private final String languageShortName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lookup_id", unique = true, updatable = false)
    private LookUpValueEntity lookUpValue;

    public LanguageEntity() {
        super();
        languageName = null;
        languageShortName = null;
    }

    @Override
    public Short getId() {
        return id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getLanguageShortName() {
        return languageShortName;
    }

    @Override
    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

    @Override
    public String getName() {
//        String name = MessageLookup.getInstance().lookup(getLookUpValue());
        return "";

    }

    @Override
    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    protected void setId(Short id) {
        this.id = id;
    }

    protected void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }
}

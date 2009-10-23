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

import java.util.Set;

import org.mifos.application.master.MessageLookup;
import org.mifos.framework.business.PersistentObject;

/**
 * Subclasses of this class provide access to the database tables which
 * correspond to enum-like classes.
 * 
 * The replacement for a subclass of this class generally will be an enum. We
 * generally expect to move looking up messages from the database for
 * localization (language and MFI) to {@link MessageLookup}.
 */
public abstract class MasterDataEntity extends PersistentObject {
    // values which override localized values are stored with locale =1
    public static Short CUSTOMIZATION_LOCALE_ID = (short) 1;

    private Short localeId;

    /** The composite primary key value */
    private Short id;

    /** The value of the lookupValue association. */
    private LookUpValueEntity lookUpValue;

    public MasterDataEntity(LookUpValueEntity lookUpValueEntity) {
        this.lookUpValue = lookUpValueEntity;
    }

    public MasterDataEntity() {        
    }
    
    public MasterDataEntity(Short id) {
        this.id = id;
    }

    public MasterDataEntity(Short id, Short localeId) {
        this.id = id;
        this.localeId = localeId;
    }

    public Short getId() {
        return id;
    }

    protected void setId(Short id) {
        this.id = id;
    }

    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

    protected void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }

    public Short getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Short localeId) {
        this.localeId = localeId;
    }

    /*
     * We ignore the locale, in order to treat values in the database as
     * customized values for all locales.
     */
    public String getName() {
        // test cases depend upon the null locale behavior
        // it seems like a hack which should be refactored
        // TODO: remove test dependency on null localeId behavior
        // if (localeId == null) {
        // return null;
        // }
        String name = MessageLookup.getInstance().lookup(getLookUpValue());
        return name;

    }

    // public String getName() {
    // return getName(getLocaleId());
    // }

    /*
     * This method is currently used just for insuring that all data is loaded
     * within a given Hibernate session.
     */
    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    /*
     * Jan 18, 2008 work in progress
     */
    protected void setName(String name) {
        MessageLookup.getInstance().updateLookupValue(getLookUpValue(), name);
    }
}

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

package org.mifos.customers.office.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.persistence.OfficeHierarchyPersistence;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.framework.exceptions.PersistenceException;

/**
 * As with the other *Entity classes, this one corresponds to
 * {@link OfficeLevel}. The extra complication is that with office levels, the
 * user can configure which are in use (for example, are there subregional
 * offices?). The <b>configured</b> flag is set/clear for levels which
 * are/are-not in use.
 */
public class OfficeLevelEntity extends MasterDataEntity {

    private final OfficeLevelEntity parent;

    private final OfficeLevelEntity child;

    private Short configured;

    private Short interactionFlag;

    public OfficeLevelEntity(OfficeLevel level) {
        super(level.getValue());
        parent = null;
        child = null;
    }

    protected OfficeLevelEntity() {
        parent = null;
        child = null;
    }

    public boolean isConfigured() {
        return this.configured > 0;
    }

    private void addConfigured(boolean configured) {
        this.configured = (short) (configured ? 1 : 0);
    }

    public boolean isInteractionFlag() {
        return this.interactionFlag > 0;
    }

    public OfficeLevelEntity getParent() {
        return parent;
    }

    public OfficeLevelEntity getChild() {
        return child;
    }

    public OfficeLevel getLevel() {
        return OfficeLevel.getOfficeLevel(this.getId());
    }

    public void update(boolean configured) throws OfficeException {
        try {
            if (!configured && new OfficeHierarchyPersistence().isOfficePresentForLevel(getLevel())) {
                throw new OfficeException(OfficeConstants.KEYHASACTIVEOFFICEWITHLEVEL);
            }
            if ((configured && !isConfigured()) || (!configured && isConfigured())) {
                addConfigured(configured);
                new OfficeHierarchyPersistence().createOrUpdate(this);
            }
        } catch (PersistenceException e) {
            throw new OfficeException(e);
        }

    }

    public void update(String name) throws OfficeException {
        setName(name);
        // try {
        // new OfficeHierarchyPersistence().createOrUpdate(this);
        // }
        // catch (PersistenceException e) {
        // throw new OfficeException(e);
        // }
    }
}

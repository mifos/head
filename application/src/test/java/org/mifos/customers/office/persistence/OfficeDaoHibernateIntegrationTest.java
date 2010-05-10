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

package org.mifos.customers.office.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.framework.MifosIntegrationTestCase;

public class OfficeDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    public OfficeDaoHibernateIntegrationTest() throws Exception {
        super();
    }

    // class under test
    private OfficeDao officeDao;

    // collaborators
    private final GenericDao genericDao = new GenericDaoHibernate();

    private final Short existingOfficeId = Short.valueOf("1");

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        officeDao = new OfficeDaoHibernate(genericDao);
    }

    public void testGivenAnOfficeExistsShouldReturnItAsOfficeDto() {

        OfficeDto office = officeDao.findOfficeDtoById(existingOfficeId);

        assertThat(office.getId(), is(Short.valueOf("1")));
        assertThat(office.getName(), is("Mifos HO"));
        assertThat(office.getSearchId(), is("1.1."));
    }

    public void testGivenActiveLevelsExistsShouldReturnThemAsOfficeViews() {

        List<OfficeDetailsDto> offices = officeDao.findActiveOfficeLevels();

        assertThat(offices.isEmpty(), is(false));
    }
}
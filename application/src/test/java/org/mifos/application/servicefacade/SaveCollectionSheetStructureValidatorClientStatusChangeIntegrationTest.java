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

package org.mifos.application.servicefacade;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;

public class SaveCollectionSheetStructureValidatorClientStatusChangeIntegrationTest extends MifosIntegrationTestCase {

    public SaveCollectionSheetStructureValidatorClientStatusChangeIntegrationTest() throws SystemException,
            ApplicationException {
        super();
    }

    private SaveCollectionSheetStructureValidator savecollectionSheetStructureValidator;
    private TestSaveCollectionSheetUtils saveCollectionSheetUtils;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        saveCollectionSheetUtils = new TestSaveCollectionSheetUtils();
        savecollectionSheetStructureValidator = new SaveCollectionSheetStructureValidator();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            saveCollectionSheetUtils.clearObjects();
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }

        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testShouldGetINVALID_CUSTOMER_STATUSIfClientClosed() throws Exception {

        /*
         * JPW - this test commented out because of the follower error which occurs
         * when changing the first clients status to closed:
         * 
         * org.hibernate.LazyInitializationException: could not initialize proxy
         * - no Session at
         * org.hibernate.proxy.AbstractLazyInitializer.initialize
         * (AbstractLazyInitializer.java:57) at
         * org.hibernate.proxy.AbstractLazyInitializer
         * .getImplementation(AbstractLazyInitializer.java:111) at
         * org.hibernate.
         * proxy.pojo.cglib.CGLIBLazyInitializer.invoke(CGLIBLazyInitializer
         * .java:150) atorg.mifos.application.personnel.business.
         * PersonnelStatusEntity$$EnhancerByCGLIB$$78c4a612.getId(<generated>)
         * at
         * org.mifos.application.personnel.business.PersonnelBO.getStatusAsEnum
         * (PersonnelBO.java:369)
         */
        /*
         * The test runs in eclipse. The test runs singly on command line using
         * mvn integration-test-Dtest=
         * SaveCollectionSheetStructureValidatorClientStatusChangeIntegrationTest
         * but not when run in mvn install
         * 
         * 
         * This test used to be in
         * SaveCollectionSheetStructureValidatorIntegrationTest but was taken
         * out to isolate it
         */

        
//         saveCollectionSheetUtils.setFirstClientClosed();
//        
//         createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.INVALID_CUSTOMER_STATUS);
    }

    private void createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason invalidReason)
            throws Exception {

        SaveCollectionSheetDto saveCollectionSheet = saveCollectionSheetUtils.createSampleSaveCollectionSheet();

        verifyInvalidReason(saveCollectionSheet, invalidReason);
    }

    private void verifyInvalidReason(SaveCollectionSheetDto saveCollectionSheet,
            InvalidSaveCollectionSheetReason invalidReason) throws Exception {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;

        try {
            savecollectionSheetStructureValidator.execute(saveCollectionSheet);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(invalidReason));
    }
}

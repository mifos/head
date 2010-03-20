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

package org.mifos.application.importexport.persistence;

import java.sql.Connection;
import java.sql.Timestamp;

import junit.framework.Assert;

import org.hibernate.exception.ConstraintViolationException;
import org.mifos.application.importexport.business.ImportedFilesEntity;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class HibernateImportedFilesDaoIntegrationTest extends MifosIntegrationTestCase {

    public HibernateImportedFilesDaoIntegrationTest() throws Exception {
        super();
    }

    @Override
    public void setUp() throws Exception {
        StaticHibernateUtil.flushAndClearSession();
        Connection connection = StaticHibernateUtil.getSessionTL().connection();
        connection.createStatement().execute("TRUNCATE TABLE IMPORTED_TRANSACTIONS_FILES");
    }

    public void testSaveAndFind() throws Exception {
        Short personnelId = new Short("1");
        PersonnelBO personnelBO = TestObjectFactory.getPersonnel(personnelId);
        Timestamp timeStamp = new Timestamp(123134554L);
        String fileName = "testFile.xls";
        ImportedFilesEntity expected = new ImportedFilesEntity(fileName, timeStamp, personnelBO);
        ImportedFilesDao importedFilesDao = new HibernateImportedFilesDao();
        StaticHibernateUtil.startTransaction();
        importedFilesDao.saveImportedFile(expected);
        StaticHibernateUtil.commitTransaction();
        ImportedFilesEntity actual = importedFilesDao.findImportedFileByName(fileName);
        Assert.assertEquals(fileName, actual.getFileName());
        Assert.assertEquals(personnelId, actual.getSubmittedBy().getPersonnelId());
        Assert.assertEquals(timeStamp, actual.getSubmittedOn());
    }

    public void testSaveConstraintVoilation() throws Exception {
        Short personnelId = new Short("1");
        PersonnelBO personnelBO = TestObjectFactory.getPersonnel(personnelId);
        Timestamp timeStamp = new Timestamp(123134554L);
        String fileName = "testFile.xls";
        ImportedFilesEntity expected = new ImportedFilesEntity(fileName, timeStamp, personnelBO);
        ImportedFilesDao importedFilesDao = new HibernateImportedFilesDao();
        StaticHibernateUtil.startTransaction();
        importedFilesDao.saveImportedFile(expected);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.flushAndClearSession();
        ImportedFilesEntity shouldViolateConstraint = new ImportedFilesEntity(fileName, timeStamp, personnelBO);
        try {
            StaticHibernateUtil.startTransaction();
            importedFilesDao.saveImportedFile(shouldViolateConstraint);
            StaticHibernateUtil.commitTransaction();
            Assert.fail("expected ConstraintViolationException");
        } catch (ConstraintViolationException e) {
        }
    }
}

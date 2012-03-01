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
 * explanation of the license and how it is applied
 */
package org.mifos.framework.image.service;

import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.domain.builders.CenterBuilder;
import org.mifos.domain.builders.ClientBuilder;
import org.mifos.domain.builders.GroupBuilder;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.image.domain.ClientPhoto;
import org.mifos.framework.image.domain.ImageInfo;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientPhotoServiceDatabaseIntegrationTest extends MifosIntegrationTestCase {

    private transient ClientBO testClient;
    private transient GroupBO testGroup;
    private transient CenterBO testCenter;
    private transient MeetingBO weeklyMeeting;

    private ClientPhotoServiceDatabase clientPhotoService; // class under testing

    @Autowired
    private GenericDao genericDao;
    @Autowired
    private HibernateTransactionHelper hibernateTransactionHelper;
    
    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    public void setHibernateTransactionHelper(HibernateTransactionHelper hibernateTransactionHelper) {
        this.hibernateTransactionHelper = hibernateTransactionHelper;
    }

    @Before
    public void setUp() throws CustomerException {
        this.clientPhotoService = new ClientPhotoServiceDatabase();
        this.clientPhotoService.setGenericDao(this.genericDao);
        this.clientPhotoService.setHibernateTransactionHelper(this.hibernateTransactionHelper);
        
        this.weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);
        
        this.testCenter = new CenterBuilder().with(this.weeklyMeeting).withName("Center Photo")
                .with(sampleBranchOffice()).withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.createCenter(this.testCenter, this.weeklyMeeting);

        this.testGroup = new GroupBuilder().withMeeting(this.weeklyMeeting).withName("Group Photo")
                .withOffice(sampleBranchOffice()).withLoanOfficer(testUser()).withParentCustomer(this.testCenter)
                .build();
        IntegrationTestObjectMother.createGroup(this.testGroup, this.weeklyMeeting);

        this.testClient = new ClientBuilder().withMeeting(this.weeklyMeeting).withName("Client 1")
                .withOffice(sampleBranchOffice()).withLoanOfficer(testUser()).withParentCustomer(this.testGroup)
                .buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(this.testClient, this.weeklyMeeting);
    }

    @Test
    public void TestCRUD() {
        final Long clientId = (long) this.testClient.getCustomerId().intValue();

        /* Create */
        final String data = "test string";
        InputStream in = new ByteArrayInputStream(data.getBytes());

        this.clientPhotoService.create(clientId, in);

        ClientPhoto cp = this.clientPhotoService.read(clientId);

        Assert.assertEquals(clientId, cp.getClientId());

        ImageInfo imageInfo = cp.getImageInfo();
        String path = imageInfo.getPath();
        Assert.assertNotNull(imageInfo.getCustomerPictureEntity().getPicture());
        Assert.assertNotNull(imageInfo.getContentType());
        Assert.assertEquals(data.length(), imageInfo.getLength().intValue());
        Assert.assertEquals(data, new String(this.clientPhotoService.getData(cp)));
        /* Update */
        final String otherData = "other test string";
        in = new ByteArrayInputStream(otherData.getBytes());
        this.clientPhotoService.update(clientId, in);
        
        cp = this.clientPhotoService.read(clientId);
        
        Assert.assertEquals(path, imageInfo.getPath());
        Assert.assertNotNull(imageInfo.getContentType());
        Assert.assertEquals(otherData.length(), imageInfo.getLength().intValue());
        Assert.assertEquals(otherData, new String(this.clientPhotoService.getData(cp)));
        Assert.assertTrue(this.clientPhotoService.delete(clientId));
        /* Delete */
        this.clientPhotoService.delete(clientId);
        
        cp = this.clientPhotoService.read(clientId);
        Assert.assertNull(cp);       
    }

    @After
    public void tearDown() {
        this.testClient = null;
        this.testGroup = null;
        this.testCenter = null;
        this.weeklyMeeting = null;

        IntegrationTestObjectMother.cleanCustomerHierarchyWithMeeting(this.testClient, this.testGroup, this.testCenter,
                this.weeklyMeeting);
    }
}

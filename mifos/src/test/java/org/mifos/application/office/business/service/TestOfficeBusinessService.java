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
 
package org.mifos.application.office.business.service;

import java.util.List;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelFixture;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestOfficeBusinessService extends MifosIntegrationTest {
	public TestOfficeBusinessService() throws SystemException, ApplicationException {
        super();
    }

    private OfficeBusinessService officeBusinessService = new OfficeBusinessService();
	private String officeSearchId;
	private PersonnelBO personnel;

	@Override
	protected void setUp() throws Exception {
		officeSearchId = "1.1";
		personnel = PersonnelFixture.createPersonnel(officeSearchId);
	}

	public void testGetActiveParents() throws NumberFormatException,
			ServiceException {
		List<OfficeView> parents = officeBusinessService.getActiveParents(
				OfficeLevel.BRANCHOFFICE, Short.valueOf("1"));
		assertEquals(2, parents.size());
		for (OfficeView view : parents) {
			if (view.getLevelId().equals(OfficeLevel.HEADOFFICE))
				assertEquals("Head Office", view.getLevelName());
			else if (view.getLevelId().equals(OfficeLevel.AREAOFFICE))
				assertEquals("Area Office", view.getLevelName());
		}
	}

	public void testGetActiveParentsFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
		 officeBusinessService.getActiveParents(OfficeLevel.BRANCHOFFICE, Short.valueOf("1"));
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			StaticHibernateUtil.closeSession();
		}

	}

	public void testGetActiveLevels() throws NumberFormatException,
			ServiceException {
		assertEquals(4, officeBusinessService.getConfiguredLevels(
				Short.valueOf("1")).size());
	}
	public void testGetActiveLevelsFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			officeBusinessService.getConfiguredLevels(
					Short.valueOf("1"));
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			StaticHibernateUtil.closeSession();
		}

	}

	public void testGetOffice() throws Exception {
		assertNotNull(officeBusinessService.getOffice(TestObjectFactory.HEAD_OFFICE));
	}
	public void testGetOfficeFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			officeBusinessService.getOffice(TestObjectFactory.HEAD_OFFICE);
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			StaticHibernateUtil.closeSession();
		}

	}
	public void testGetStatusList() throws NumberFormatException,
			ServiceException {
		assertEquals(2, officeBusinessService.getStatusList(Short.valueOf("1"))
				.size());
	}
	public void testGetStatusListFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			officeBusinessService.getStatusList(Short.valueOf("1"));
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			StaticHibernateUtil.closeSession();
		}

	}
	public void testGetBranchOffices() throws ServiceException {
		assertEquals(1, officeBusinessService.getBranchOffices().size());
	}
	public void testGetBranchOfficesFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			officeBusinessService.getBranchOffices();
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			StaticHibernateUtil.closeSession();
		}

	}
	public void testGetOfficesTillBranchOffice() throws ServiceException {
		assertEquals(2, officeBusinessService.getOfficesTillBranchOffice()
				.size());
	}
	public void testGetOfficesTillBranchOfficeFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			officeBusinessService.getOfficesTillBranchOffice();
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			StaticHibernateUtil.closeSession();
		}

	}
	public void testGetChildOffices() throws ServiceException {
		OfficeBO headOffice = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		List<OfficeView> officeList = officeBusinessService
				.getChildOffices(headOffice.getSearchId());
		assertEquals(3, officeList.size());
		officeList = null;
		headOffice = null;
	}
	public void testGetChildOfficesFailure() throws Exception{
		OfficeBO headOffice = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		TestObjectFactory.simulateInvalidConnection();
		try{
			 officeBusinessService
				.getChildOffices(headOffice.getSearchId());
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			StaticHibernateUtil.closeSession();
		}

	}
	public void testGetBranchesUnderUser() throws Exception {
		List<OfficeBO> officeList = officeBusinessService
				.getActiveBranchesUnderUser(personnel);
		assertNotNull(officeList);
		assertEquals(1, officeList.size());
	}
	public void testGetBranchesUnderUserFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			officeBusinessService
			.getActiveBranchesUnderUser(personnel);
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			StaticHibernateUtil.closeSession();
		}

	}

	public void testGetAllofficesForCustomFIeld() throws Exception {
		List<OfficeBO> officeList = officeBusinessService
				.getAllofficesForCustomFIeld();
		assertNotNull(officeList);
		assertEquals(3, officeList.size());
	}
}

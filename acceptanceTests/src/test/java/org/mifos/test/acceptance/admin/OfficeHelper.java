/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineNewOfficePage;
import org.mifos.test.acceptance.framework.admin.ViewOfficesPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;

public class OfficeHelper {

    private final NavigationHelper navigationHelper;

    public OfficeHelper(NavigationHelper navigationHelper) {
        this.navigationHelper = navigationHelper;
    }

    public void defineOffice(String officeName, String shortName, String officeType, String parentOffice) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineNewOfficePage defineNewOfficePage = adminPage.navigateToDefineANewOfficePage();
        defineNewOfficePage.setOfficeName(officeName);
        defineNewOfficePage.setOfficeShortName(shortName);
        defineNewOfficePage.setOfficeType(officeType);
        defineNewOfficePage.setParentOffice(parentOffice);
        defineNewOfficePage.preview();
        defineNewOfficePage.submit();
    }

    public void viewOffice(String[] expectedData) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewOfficesPage viewOfficesPage = adminPage.navigateToViewOfficesPage();
        viewOfficesPage.verifyPage();
        viewOfficesPage.verifyOfficeList(expectedData);
    }
}

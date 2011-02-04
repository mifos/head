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

package org.mifos.test.acceptance.framework.testhelpers;

import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPreviewDataPage;

public class ClientTestHelper {

    public static final String ACTIVE = "Active";
    public static final String PENDING_APPROVAL = "Application Pending Approval";
    public static final String PARTIAL_APPLICATION = "Partial Application";

    public ClientViewDetailsPage changeCustomerStatus(ClientViewDetailsPage clientDetailsPage) {

        CustomerChangeStatusPage statusChangePage = clientDetailsPage.navigateToCustomerChangeStatusPage();
        CustomerChangeStatusPage.SubmitFormParameters statusParameters = new CustomerChangeStatusPage.SubmitFormParameters();
        statusParameters.setStatus(CustomerChangeStatusPage.SubmitFormParameters.PARTIAL_APPLICATION);
        statusParameters.setNotes("Status change");

        CustomerChangeStatusPreviewDataPage statusPreviewPage = statusChangePage.submitAndGotoCustomerChangeStatusPreviewDataPage(statusParameters);
        ClientViewDetailsPage clientDetailsPage2 = statusPreviewPage.submitAndGotoClientViewDetailsPage();
        clientDetailsPage2.verifyStatus(PARTIAL_APPLICATION);
        clientDetailsPage2.verifyNotes(statusParameters.getNotes());


        CustomerChangeStatusPage statusChangePage2 = clientDetailsPage2.navigateToCustomerChangeStatusPage();
        statusParameters.setStatus(CustomerChangeStatusPage.SubmitFormParameters.PENDING_APPROVAL);
        statusParameters.setNotes("notes");
        CustomerChangeStatusPreviewDataPage statusPreviewPage2 =
            statusChangePage2.submitAndGotoCustomerChangeStatusPreviewDataPage(statusParameters);


        ClientViewDetailsPage clientDetailsPage3 = statusPreviewPage2.submitAndGotoClientViewDetailsPage();
        clientDetailsPage3.verifyNotes(statusParameters.getNotes());
        clientDetailsPage2.verifyStatus(PENDING_APPROVAL);

        CustomerChangeStatusPage statusChangePage3 = clientDetailsPage3.navigateToCustomerChangeStatusPage();
        statusParameters.setStatus(CustomerChangeStatusPage.SubmitFormParameters.ACTIVE);
        statusParameters.setNotes("notes");
        CustomerChangeStatusPreviewDataPage statusPreviewPage3 =
            statusChangePage3.submitAndGotoCustomerChangeStatusPreviewDataPage(statusParameters);


        ClientViewDetailsPage clientDetailsPage4 = statusPreviewPage3.submitAndGotoClientViewDetailsPage();
        clientDetailsPage4.verifyNotes(statusParameters.getNotes());
        clientDetailsPage3.verifyStatus(ACTIVE);

        CustomerChangeStatusPage statusChangePage4 = clientDetailsPage4.navigateToCustomerChangeStatusPage();

        ClientViewDetailsPage clientDetailsPage5 = statusChangePage4.cancelAndGotoClientViewDetailsPage(statusParameters);
        clientDetailsPage5.verifyNotes(statusParameters.getNotes());
        return clientDetailsPage5;
    }
}

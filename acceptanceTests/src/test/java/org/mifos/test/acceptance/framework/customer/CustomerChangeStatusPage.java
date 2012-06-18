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

package org.mifos.test.acceptance.framework.customer;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.EditCustomerStatusParameters;
import org.mifos.test.acceptance.framework.group.GroupStatus;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.client.ClientStatus;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;

public class CustomerChangeStatusPage extends MifosPage {

    public CustomerChangeStatusPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("CustomerChangeStatus");
    }

    private void setChangeStatusParameters(EditCustomerStatusParameters customerStatusParameters) {
        if(customerStatusParameters.getGroupStatus()!=null)
        {
            selenium.check("name=newStatusId value=" + customerStatusParameters.getGroupStatus().getId());
            selenium.fireEvent("name=newStatusId value=" + customerStatusParameters.getGroupStatus().getId(), "click");
            if(customerStatusParameters.getGroupStatus().equals(GroupStatus.CANCELLED)){
                selenium.select("customerchangeStatus.input.cancel_reason", "value="+customerStatusParameters.getCancelReason().getId());
            }
            if(customerStatusParameters.getGroupStatus().equals(GroupStatus.CLOSED)){
                selenium.select("customerchangeStatus.input.cancel_reason", "value="+customerStatusParameters.getCloseReason().getId());
            }
        }
        if(customerStatusParameters.getClientStatus()!=null){
            selenium.check("name=newStatusId value=" + customerStatusParameters.getClientStatus().getId());
            selenium.fireEvent("name=newStatusId value=" + customerStatusParameters.getClientStatus().getId(), "click");
            boolean statusIsClosed = customerStatusParameters.getClientStatus().equals(ClientStatus.CLOSED) && customerStatusParameters.getClientCloseReason()!=null;
            boolean statusIsCancelled = customerStatusParameters.getClientStatus().equals(ClientStatus.CANCELLED) && customerStatusParameters.getCancelReason()!=null;
            if(statusIsClosed) {
                selenium.select("customerchangeStatus.input.cancel_reason", "value="+customerStatusParameters.getClientCloseReason().getId());
            }
            else if(statusIsCancelled){
                selenium.select("customerchangeStatus.input.cancel_reason", "value="+customerStatusParameters.getCancelReason().getId());
            }
        }
        if(customerStatusParameters.getCenterStatus() != null) {
            selenium.check("name=newStatusId value=" + customerStatusParameters.getCenterStatus().getId());
        }
        selenium.type("customerchangeStatus.input.notes", customerStatusParameters.getNote());
        selenium.click("customerchangeStatus.button.preview");
        waitForPageToLoad();
    }

    public CustomerChangeStatusPreviewPage setChangeStatusParametersAndSubmit(EditCustomerStatusParameters customerStatusParameters){
        setChangeStatusParameters(customerStatusParameters);
        return new CustomerChangeStatusPreviewPage(selenium);
    }

    public QuestionResponsePage changeStatusAndNavigateToQuestionResponsePage(EditCustomerStatusParameters customerStatusParameters){
        setChangeStatusParameters(customerStatusParameters);
        return new QuestionResponsePage(selenium);
    }

    public ClientViewDetailsPage cancelAndGotoClientViewDetailsPage() {
        selenium.click("customerchangeStatus.button.cancel");
        waitForPageToLoad();
        return new ClientViewDetailsPage(selenium);
    }
}

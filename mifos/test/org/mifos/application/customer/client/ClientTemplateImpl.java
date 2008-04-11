/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
package org.mifos.application.customer.client;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.mifos.application.customer.CustomerTemplateImpl;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.NameType;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientTemplateImpl extends CustomerTemplateImpl implements ClientTemplate {
    private Date mfiJoiningDate;
    private List<SavingsOfferingBO> savingsOfferings;
    private Short formedById;
    private Short officeId;
    private Integer parentCustomerId;
    private Date dateOfBirth;
    private String governmentId;
    private Short trained;
    private Date trainedDate;
    private Short groupFlag;
    private ClientNameDetailView clientNameDetail;
    private ClientNameDetailView spouseNameDetail;
    private ClientDetailView clientDetail;
    private InputStream picture;

    private ClientTemplateImpl(Short officeId, Integer parentCustomerId) {
        super("TestClient", CustomerStatus.CLIENT_ACTIVE);
        this.officeId = officeId;
        this.parentCustomerId = parentCustomerId;
        this.formedById = PersonnelConstants.SYSTEM_USER;
        this.clientDetail = new ClientDetailView(1, 1, 1,
					1, 1, 1, Short.valueOf("1"), Short.valueOf("1"), Short
							.valueOf("41"));
        this.clientNameDetail = new ClientNameDetailView(
					NameType.MAYBE_CLIENT, TestObjectFactory.SAMPLE_SALUTATION, getDisplayName(),
					"middle", getDisplayName(), "secondLast");
        this.spouseNameDetail = new ClientNameDetailView(
				NameType.SPOUSE, TestObjectFactory.SAMPLE_SALUTATION,
                "TestMaybeClient", "middle", getDisplayName(), "secondLast");
        // TODO: this should be fixed - I mean really...
        this.dateOfBirth = new Date(1222333444000L);
        this.groupFlag = YesNoFlag.YES.getValue();
    }

    public Date getMfiJoiningDate() {
        return this.mfiJoiningDate;
    }

    public List<SavingsOfferingBO> getOfferingsSelected() {
        return this.savingsOfferings;
    }

    public Short getFormedById() {
        return this.formedById;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public Integer getParentCustomerId() {
        return this.parentCustomerId;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public String getGovernmentId() {
        return this.governmentId;
    }

    public Short getTrained() {
        return this.trained;
    }

    public Date getTrainedDate() {
        return this.trainedDate;
    }

    public Short getGroupFlag() {
        return this.groupFlag;
    }

    public ClientNameDetailView getClientNameDetailView() {
        return this.clientNameDetail;
    }

    public ClientNameDetailView getSpouseNameDetailView() {
        return this.spouseNameDetail;
    }

    public ClientDetailView getClientDetailView() {
        return this.clientDetail;
    }

    public InputStream getPicture() {
        return this.picture;
    }

    public static ClientTemplateImpl createActiveGroupClientTemplate(Short officeId, Integer parentCustomerId) {
        return new ClientTemplateImpl(officeId, parentCustomerId);
    }
}

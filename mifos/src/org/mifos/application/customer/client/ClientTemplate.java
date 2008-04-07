package org.mifos.application.customer.client;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.mifos.application.customer.CustomerTemplate;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;

/**
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * <p/>
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license
 * and how it is applied.
 */
public interface ClientTemplate extends CustomerTemplate {
    public Date getMfiJoiningDate();

    public List<SavingsOfferingBO> getOfferingsSelected();

    public Short getFormedById();

    public Short getOfficeId();

    public Integer getParentCustomerId();

    public Date getDateOfBirth();

    public String getGovernmentId();

    public Short getTrained();

    public Date getTrainedDate();

    public Short getGroupFlag();

    public ClientNameDetailView getClientNameDetailView();

    public ClientNameDetailView getSpouseNameDetailView();

    public ClientDetailView getClientDetailView();

    public InputStream getPicture();
}

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

package org.mifos.application.bulkentry.util.helpers;

import java.sql.Date;
import java.util.List;

import org.mifos.application.bulkentry.business.CollectionSheetEntryView;
import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class BulkEntryClientAttendanceThread implements Runnable {

    List<CollectionSheetEntryView> customerViews;

    List<ClientBO> clients;

    List<String> customerNames;

    Date meetingDate;

    StringBuffer isThreadDone;

    public BulkEntryClientAttendanceThread(List<CollectionSheetEntryView> customerViews, List<ClientBO> clients,
            List<String> customerNames, Date meetingDate, StringBuffer isThreadDone) {
        this.customerViews = customerViews;
        this.clients = clients;
        this.customerNames = customerNames;
        this.meetingDate = meetingDate;
        this.isThreadDone = isThreadDone;
    }

    public void run() {
        try {
            BulkEntryBusinessService bulkEntryBusinessService = new BulkEntryBusinessService();
            for (CollectionSheetEntryView parent : customerViews) {
                Short levelId = parent.getCustomerDetail().getCustomerLevelId();
                if (levelId.equals(CustomerLevel.CLIENT.getValue())) {
                    try {
                        bulkEntryBusinessService.setClientAttendance(parent.getCustomerDetail().getCustomerId(),
                                meetingDate, parent.getAttendence(), clients);
                    } catch (ServiceException e) {
                        customerNames.add(parent.getCustomerDetail().getDisplayName());
                        HibernateUtil.closeSession();
                    }
                }
            }
        } finally {
            HibernateUtil.closeSession();
            isThreadDone.append("Done");
        }
    }

}

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.persistence.ClientPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;

/**
 *
 */
public class ClientAttendanceAssembler {

    private final ClientPersistence clientPersistence;
    private final ClientAttendanceDao clientAttendanceDao;

    public ClientAttendanceAssembler(ClientPersistence clientPersistence, ClientAttendanceDao clientAttendanceDao) {
        this.clientPersistence = clientPersistence;
        this.clientAttendanceDao = clientAttendanceDao;
    }

    public List<ClientAttendanceBO> fromDto(final List<CollectionSheetEntryView> collectionSheetEntryViews,
            final Short branchId, final String searchId, final Date transactionDate) {

        final LocalDate meetingDate = new LocalDate(transactionDate);
        List<ClientAttendanceBO> clientAttendanceList = null;
        
        try {
            clientAttendanceList = clientAttendanceDao.findClientAttendance(branchId, searchId, meetingDate);

        for (CollectionSheetEntryView collectionSheetEntryView : collectionSheetEntryViews) {
            final Short levelId = collectionSheetEntryView.getCustomerDetail().getCustomerLevelId();

            if (levelId.equals(CustomerLevel.CLIENT.getValue())) {
                ClientBO client = clientPersistence.getClient(collectionSheetEntryView.getCustomerDetail()
                        .getCustomerId());
                ClientAttendanceBO clientAttendance = findClientAttendance(clientAttendanceList, client);
                if (clientAttendance == null) {
                    clientAttendance = new ClientAttendanceBO();
                    clientAttendance.setCustomer(client);
                    clientAttendance.setMeetingDate(meetingDate.toDateMidnight().toDate());
                }
                clientAttendance.setAttendance(collectionSheetEntryView.getAttendence());
                clientAttendanceList.add(clientAttendance);
            }
        }
        }
        catch (PersistenceException e) {
            throw new MifosRuntimeException("Failure when assembling client attendance list.", e);
                                    }
        return clientAttendanceList;
    }

    private ClientAttendanceBO findClientAttendance(final List<ClientAttendanceBO> clientAttendanceList, final ClientBO client) {
        if (clientAttendanceList != null && clientAttendanceList.size() > 0) {
            for (ClientAttendanceBO clientAttendance : clientAttendanceList) {
                if (clientAttendance.getCustomer() == client) {
                    return clientAttendance;
                }
            }
        }
        return null;
    }
}

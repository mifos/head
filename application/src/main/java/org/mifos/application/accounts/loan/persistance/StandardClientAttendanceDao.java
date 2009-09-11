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

package org.mifos.application.accounts.loan.persistance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.joda.time.LocalDate;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 *
 */
public class StandardClientAttendanceDao implements ClientAttendanceDao {

    /*
     * FIXME - keithw - no need to have MasterPersistence, DAO should
     * extend/composed of base class implementing hibernate functionality.
     */
    private final MasterPersistence masterPersistence;
    private HibernateUtil hibernateUtil;

    public StandardClientAttendanceDao(final MasterPersistence masterPersistence) {
        this.masterPersistence = masterPersistence;
    }

    @SuppressWarnings("unchecked")
    public List<ClientAttendanceDto> findClientAttendanceForOffice(final Date meetingDate, final Short officeId,
            final String searchId) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("MEETING_DATE", meetingDate);
        queryParameters.put("OFFICE_ID", officeId);
        queryParameters.put("SEARCH_ID", searchId + ".%");

        try {
            List<ClientAttendanceDto> queryResult = masterPersistence.executeNamedQuery("ClientAttendance.getAttendanceForOffice",
                    queryParameters);
            
            return queryResult;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    public ClientAttendanceBO findClientAttendance(final Integer clientId, final LocalDate meetingDate) {
        
        ClientAttendanceBO result;
        try {
            Map<String, Object> queryParameters = new HashMap<String, Object>();
            queryParameters.put("CUSTOMER_ID", clientId);
            queryParameters.put("MEETING_DATE", meetingDate.toDateMidnight().toDate());
            result = (ClientAttendanceBO) masterPersistence.execUniqueResultNamedQuery(
                    "ClientAttendance.getAttendanceForClientAndMeetingDate", queryParameters);
            return result;
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        } catch (PersistenceException pe) {
            throw new RuntimeException(pe);
        }
    }
    
    public void save(final List<ClientAttendanceBO> clientAttendances) {
        Session session = getHibernateUtil().getSessionTL();

        for (ClientAttendanceBO clientAttendanceBO : clientAttendances) {
            session.saveOrUpdate(clientAttendanceBO);
        }
        session.flush();
    }
    
    private HibernateUtil getHibernateUtil() {
        if (null == hibernateUtil) {
            hibernateUtil = new HibernateUtil();
        }
        return hibernateUtil;
    }
}

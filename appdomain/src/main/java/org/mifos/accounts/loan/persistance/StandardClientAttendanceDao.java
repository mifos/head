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

package org.mifos.accounts.loan.persistance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.joda.time.LocalDate;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.customers.client.business.ClientAttendanceBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class StandardClientAttendanceDao implements ClientAttendanceDao {

    @Autowired
    private LegacyMasterDao legacyMasterDao;

    @Override
    @SuppressWarnings("unchecked")
    public List<ClientAttendanceBO> findClientAttendance(final Short branchId, final String searchId,
            final LocalDate meetingDate) throws PersistenceException {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("BRANCH_ID", branchId);
        queryParameters.put("SEARCH_ID", searchId + ".%");
        queryParameters.put("MEETING_DATE", meetingDate.toDateMidnight().toDate());
        return legacyMasterDao.executeNamedQuery("ClientAttendance.getAttendanceForClientsOnMeetingDate",
                queryParameters);
    }

    @Override
    public void save(final List<ClientAttendanceBO> clientAttendances) {
        Session session = StaticHibernateUtil.getSessionTL();

        for (ClientAttendanceBO clientAttendanceBO : clientAttendances) {
            session.saveOrUpdate(clientAttendanceBO);
        }
    }
}

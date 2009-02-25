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

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class StandardClientAttendanceDao implements ClientAttendanceDao {

    private CustomerPersistence customerPersistence;
    private MasterPersistence masterPersistence;

    public StandardClientAttendanceDao() {
    }
    
    @Override
    public AttendanceType getAttendance(Integer clientId, LocalDate meetingDate) throws PersistenceException {
        ClientAttendanceBO result = getClientAttendance(clientId, meetingDate);
        if (result == null) {
            throw new PersistenceException("Could not find attendance for clientId " + clientId + " and meeting date " + meetingDate.toDateMidnight().toDate());
        }
        return result.getAttendanceAsEnum();
    }

    @Override
    public void setAttendance(Integer clientId, LocalDate meetingDate, AttendanceType attendance)
            throws PersistenceException {
        CustomerBO customer = getCustomerPersistence().getCustomer(clientId);
        ClientAttendanceBO clientAttendance = getClientAttendance(clientId, meetingDate);
        if (null == clientAttendance) {
            clientAttendance = new ClientAttendanceBO();
            clientAttendance.setCustomer(customer);
            clientAttendance.setMeetingDate(meetingDate.toDateMidnight().toDate());
        }
        clientAttendance.setAttendance(attendance);
        getCustomerPersistence().createOrUpdate(clientAttendance);
        HibernateUtil.commitTransaction();
    }

    private ClientAttendanceBO getClientAttendance(Integer clientId, LocalDate meetingDate) throws PersistenceException {
        ClientAttendanceBO result;
        try {
            Map<String, Object> queryParameters = new HashMap<String, Object>();
            queryParameters.put("CUSTOMER_ID", clientId);
            queryParameters.put("MEETING_DATE", meetingDate.toDateMidnight().toDate());
            result = (ClientAttendanceBO) getMasterPersistence().execUniqueResultNamedQuery("ClientAttendance.getAttendanceForClientAndMeetingDate", queryParameters);
            return result;
        } catch (NumberFormatException e) {
            throw new PersistenceException(e);
        }
    }
    
    
    private CustomerPersistence getCustomerPersistence() {
        if (null == customerPersistence) {
            customerPersistence = new CustomerPersistence();
        }
        return customerPersistence;
    }

    public void setCustomerPersistence(CustomerPersistence customerPersistence) {
            this.customerPersistence = customerPersistence;
        }

    public MasterPersistence getMasterPersistence() {
        if (null == masterPersistence) {
           masterPersistence = new MasterPersistence();
        }
        return masterPersistence;
    }

    public void setMasterPersistence(MasterPersistence masterPersistence) {
        this.masterPersistence = masterPersistence;
    }

}

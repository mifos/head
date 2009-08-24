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

package org.mifos.application.customer.client.business.service;

import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;

/**
 * StandardClientService encapsulates high level operations on clients including
 * client creation and retrieval. Client attendance operations are included here
 * too. No domain/business objects should be returned from this class, only Data
 * Transfer Objects (DTOs) or primitives.
 * 
 */
public class StandardClientService implements ClientService {

    private final ClientAttendanceDao clientAttendanceDao;

    public StandardClientService(ClientAttendanceDao clientAttendanceDao) {
        this.clientAttendanceDao = clientAttendanceDao;
    }

    public HashMap<Integer, ClientAttendanceDto> getClientAttendance(List<ClientAttendanceDto> clientAttendanceDtos)
            throws ServiceException {
        
        final HashMap<Integer, ClientAttendanceDto> clientAttendance = new HashMap<Integer, ClientAttendanceDto>();
        ClientAttendanceDto newClientAttendanceDto = null;
        for (ClientAttendanceDto clientAttendanceDto : clientAttendanceDtos) {
            try {
                final LocalDate meetingDate = clientAttendanceDto.getMeetingDate();
                final Integer clientId = clientAttendanceDto.getClientId();
                final AttendanceType attendanceType = clientAttendanceDao.getAttendance(clientId, meetingDate);
                newClientAttendanceDto = new ClientAttendanceDto(clientId, meetingDate, attendanceType);
                clientAttendance.put(clientId, newClientAttendanceDto);
            } catch (PersistenceException e) {
                throw new ServiceException(e);
            }
        }
        return clientAttendance;
    }
}

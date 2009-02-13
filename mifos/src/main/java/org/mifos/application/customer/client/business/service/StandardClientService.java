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
import org.mifos.application.accounts.loan.persistance.ClientDao;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.framework.business.service.Service;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;

/**
 * StandardClientService encapsulates high level operations on clients
 * including client creation and retrieval. Client attendance
 * operations are included here too. No domain/business objects 
 * should be returned from this class, only Data Transfer Objects 
 * (DTOs) or primitives.
 *
 */
public class StandardClientService implements ClientService {
    
    private ClientDao clientDao;
    private ClientAttendanceDao clientAttendanceDao;
    
    public StandardClientService() {
        // for use with setter dependency injection
    }
    
    public StandardClientService(ClientDao clientDao, ClientAttendanceDao clientAttendanceDao) {
        this.clientDao = clientDao;
        this.clientAttendanceDao = clientAttendanceDao;
    }
    
    @Override
    public HashMap<Integer, ClientAttendanceDto> getClientAttendance(List<ClientAttendanceDto> clientAttendanceDtos) throws ServiceException {
        HashMap<Integer, ClientAttendanceDto> clientAttendance = new HashMap<Integer, ClientAttendanceDto>();
        ClientAttendanceDto newClientAttendanceDto = null;
        for (ClientAttendanceDto clientAttendanceDto : clientAttendanceDtos) {
            try {
                LocalDate meetingDate = clientAttendanceDto.getMeetingDate();
                Integer clientId = clientAttendanceDto.getClientId();
                newClientAttendanceDto = new ClientAttendanceDto(clientId, meetingDate, clientAttendanceDao.getAttendance(clientId, meetingDate));
                clientAttendance.put(clientId, newClientAttendanceDto);
            } catch (PersistenceException e) {
                throw new ServiceException(e);
            }
        }
        return clientAttendance;
    }
    
    public ClientDao getClientDao() {
        return this.clientDao;
    }

    public void setClientDao(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    public ClientAttendanceDao getClientAttendanceDao() {
        return this.clientAttendanceDao;
    }

    public void setClientAttendanceDao(ClientAttendanceDao clientAttendanceDao) {
        this.clientAttendanceDao = clientAttendanceDao;
    }

}

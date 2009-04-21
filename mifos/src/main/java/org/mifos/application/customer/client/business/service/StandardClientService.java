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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.accounts.loan.persistance.ClientDao;
import org.mifos.application.accounts.loan.persistance.StandardClientAttendanceDao;
import org.mifos.application.accounts.loan.persistance.StandardClientDao;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
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
        this.clientDao = new StandardClientDao();
        this.clientAttendanceDao = new StandardClientAttendanceDao();
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
    
    public HashMap<Integer, ClientAttendanceDto> getClientAttendance(Date meetingDate, Short officeId) throws ServiceException {
        HashMap<Integer, ClientAttendanceDto> clientAttendance = new HashMap<Integer, ClientAttendanceDto>();
        try {
            for (ClientAttendanceBO clientAttendanceBO : clientAttendanceDao.getClientAttendance(meetingDate, officeId)) {
                ClientAttendanceDto clientAttendanceDto = new ClientAttendanceDto(clientAttendanceBO.getId(), clientAttendanceBO.getMeetingDate(), clientAttendanceBO.getAttendance()); 
                clientAttendance.put(clientAttendanceBO.getId(), clientAttendanceDto);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        return clientAttendance;
    }
    
    public List<ClientAttendanceDto> getClientAttendanceList(Date meetingDate, Short officeId) throws ServiceException {
        List<ClientAttendanceBO> clientAttendanceBos = null;
        try {
            clientAttendanceBos = clientAttendanceDao.getClientAttendance(meetingDate, officeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        ArrayList<ClientAttendanceDto> result = new ArrayList<ClientAttendanceDto>();
        for (ClientAttendanceBO clientAttendanceBo : clientAttendanceBos) {
            result.add(new ClientAttendanceDto(clientAttendanceBo.getId(), clientAttendanceBo.getMeetingDate(), clientAttendanceBo.getAttendance()));
        }
        return result;
    }

    public void setClientAttendance(List<ClientAttendanceDto> clientAttendanceDtos) throws ServiceException {
        for (ClientAttendanceDto clientAttendanceDto : clientAttendanceDtos) {
            setClientAttendance(clientAttendanceDto);
        }
    }

    public void setClientAttendance(ClientAttendanceDto clientAttendanceDto) throws ServiceException {
        try {
            clientAttendanceDao.setAttendance(clientAttendanceDto.getClientId(), clientAttendanceDto.getMeetingDate(), clientAttendanceDto.getAttendance());
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
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

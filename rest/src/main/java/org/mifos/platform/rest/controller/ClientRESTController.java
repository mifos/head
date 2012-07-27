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
package org.mifos.platform.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.CenterServiceFacade;
import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.application.servicefacade.CreateClientCreationDetail;
import org.mifos.application.servicefacade.CreateClientNameDetailDto;
import org.mifos.application.servicefacade.CreatePersonalDetailDto;
import org.mifos.application.servicefacade.CreationAddresDto;
import org.mifos.application.servicefacade.CreationFeeDto;
import org.mifos.application.servicefacade.CreationMeetingDto;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.ClientCreationDetail;
import org.mifos.dto.domain.CustomerChargesDetailsDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.screen.ClientInformationDto;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreateClientNameDetailDtoMixIn;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreateGroupCreationDetailDtoMixIn;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreatePersonalDetailDtoMixIn;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreationAddresDtoMixIn;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreationFeeDtoMixIn;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreationMeetingDtoMixIn;
import org.mifos.platform.rest.controller.RESTAPIHelper.ErrorMessage;
import org.mifos.platform.rest.controller.validation.ParamValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ClientRESTController {

    @Autowired
    private ClientServiceFacade clientServiceFacade;

    @Autowired
    private CenterServiceFacade centerServiceFacade;

    @Autowired
    private CustomerDao customerDao;

    @RequestMapping(value = "client/num-{globalCustNum}", method = RequestMethod.GET)
    public @ResponseBody
    ClientInformationDto getClientByNumber(@PathVariable String globalCustNum) {
        return clientServiceFacade.getClientInformationDto(globalCustNum);
    }

    @RequestMapping(value = "client/num-{globalCustNum}/charges", method = RequestMethod.GET)
    public @ResponseBody
    CustomerChargesDetailsDto getClientChargesByNumber(@PathVariable String globalCustNum) {
        ClientBO clientBO = customerDao.findClientBySystemId(globalCustNum);

        CustomerChargesDetailsDto clientCharges = centerServiceFacade.retrieveChargesDetails(clientBO.getCustomerId());
        clientCharges.addActivities(centerServiceFacade.retrieveRecentActivities(clientBO.getCustomerId(), 3));

        return clientCharges;
    }

    @RequestMapping(value = "client/create", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> createClient(@RequestBody String request) throws Throwable {
        ObjectMapper om = createClientMapping();

        CreateClientCreationDetail creationDetail = null;
        MeetingBO meetingBO = null;

        try {
            creationDetail = om.readValue(request, CreateClientCreationDetail.class);
        } catch (JsonMappingException e) {
            e.getCause();
        }
        validate(creationDetail);
        meetingBO = (MeetingBO) creationDetail.getMeeting().toBO();
        ClientCreationDetail client = createClient(creationDetail);

        CustomerDetailsDto clientDetails = clientServiceFacade.createNewClient(client, meetingBO.toDto(), null);
        ClientInformationDto clientInfo = clientServiceFacade.getClientInformationDto(clientDetails.getGlobalCustNum());

        Map<String, String> map = new HashMap<String, String>();
        map.put("status", "success");
        map.put("globalCustNum", clientInfo.getClientDisplay().getGlobalCustNum());
        map.put("accountNum", clientInfo.getCustomerAccountSummary().getGlobalAccountNum());
        return map;
    }

    private ObjectMapper createClientMapping() {
        ObjectMapper om = new ObjectMapper();
        om.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        om.getDeserializationConfig().addMixInAnnotations(CreateClientCreationDetail.class,
                CreateGroupCreationDetailDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreatePersonalDetailDto.class,
                CreatePersonalDetailDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreateClientNameDetailDto.class,
                CreateClientNameDetailDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationAddresDto.class, CreationAddresDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationMeetingDto.class, CreationMeetingDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationFeeDto.class, CreationFeeDtoMixIn.class);
        om.getJsonFactory().configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        return om;
    }

    private ClientCreationDetail createClient(CreateClientCreationDetail creationDetail) {
        return new ClientCreationDetail(creationDetail.getClientNameDetail().getDisplayName(),
                creationDetail.getCustomerStatus(), creationDetail.getMfiJoiningDate().toDateMidnight().toDate(),
                creationDetail.getExternalId(), creationDetail.getAddress().toDto(), creationDetail.getFormedBy(),
                creationDetail.getDateOfBirth().toDateMidnight().toDate(), creationDetail.getGovernmentId(),
                creationDetail.getTrained(), creationDetail.getTrainedDate().toDateMidnight().toDate(), creationDetail.getGroupFlag(),
                creationDetail.getClientNameDetail().toDto(), creationDetail.getPersonalDetail().toDto(),
                creationDetail.feeAsAccountFeeDto(creationDetail.getAccountFees()), creationDetail.getParentGroupId(),
                creationDetail.getLoanOfficerId(), creationDetail.getOfficeId(), creationDetail.getActivationDate());
    }

    private void validate(CreateClientCreationDetail creationDetail) throws ParamValidationException {
        validateMeeting(creationDetail);
        validateGroupData(creationDetail);
    }

    private void validateMeeting(CreateClientCreationDetail creationDetail) throws ParamValidationException {
        if (null == creationDetail.getMeeting() && null == creationDetail.getParentGroupId()) {
            throw new ParamValidationException(ErrorMessage.INVALID_MEETING);
        }
    }

    private void validateGroupData(CreateClientCreationDetail creationDetail) throws ParamValidationException {
        if (null == creationDetail.getLoanOfficerId()) {
            throw new ParamValidationException(ErrorMessage.INVALID_LOAN_OFFICER_ID);
        }
        if (null == creationDetail.getOfficeId()) {
            throw new ParamValidationException(ErrorMessage.INVALID_OFFICE_ID);
        }
    }

}

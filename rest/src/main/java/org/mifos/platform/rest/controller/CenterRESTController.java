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
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.CenterServiceFacade;
import org.mifos.application.servicefacade.CreateCenterDetailsDto;
import org.mifos.application.servicefacade.CreationAddresDto;
import org.mifos.application.servicefacade.CreationFeeDto;
import org.mifos.application.servicefacade.CreationMeetingDto;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.CenterCreationDetail;
import org.mifos.dto.domain.CenterInformationDto;
import org.mifos.dto.domain.CustomerChargesDetailsDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.platform.rest.controller.RESTAPIHelper.CenterCreationDetailMixIn;
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
public class CenterRESTController {

    @Autowired
    private CenterServiceFacade centerServiceFacade;

    @Autowired
    private CustomerDao customerDao;

    @RequestMapping(value = "center/num-{globalCustNum}", method = RequestMethod.GET)
    public @ResponseBody
    CenterInformationDto getCenterByNumber(@PathVariable String globalCustNum) {
        return centerServiceFacade.getCenterInformationDto(globalCustNum);
    }

    @RequestMapping(value = "center/num-{globalCustNum}/charges", method = RequestMethod.GET)
    public @ResponseBody
    CustomerChargesDetailsDto getCenterChargesByNumber(@PathVariable String globalCustNum) {
        CenterBO centerBO = customerDao.findCenterBySystemId(globalCustNum);

        CustomerChargesDetailsDto centerCharges = centerServiceFacade.retrieveChargesDetails(centerBO.getCustomerId());
        centerCharges.addActivities(centerServiceFacade.retrieveRecentActivities(centerBO.getCustomerId(), 3));

        return centerCharges;
    }

    @RequestMapping(value = "/center/create", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> createCenter(@RequestBody String request) throws Throwable {
        ObjectMapper om = createCenterMaping();

        CreateCenterDetailsDto creationDetail = null;
        MeetingBO meetingBO = null;

        try {
            creationDetail = om.readValue(request, CreateCenterDetailsDto.class);
        } catch (JsonMappingException e) {
            throw e.getCause();
        }
        validate(creationDetail);

        meetingBO = (MeetingBO) creationDetail.getMeeting().toBO();

        CenterCreationDetail center = createCenter(creationDetail);
        CustomerDetailsDto details = this.centerServiceFacade.createNewCenter(center, meetingBO.toDto());
        CenterInformationDto centerInfo = this.centerServiceFacade.getCenterInformationDto(details.getGlobalCustNum());

        Map<String, String> map = new HashMap<String, String>();
        map.put("status", "success");
        map.put("globalCustNum", centerInfo.getCenterDisplay().getGlobalCustNum());
        map.put("accountNum", centerInfo.getCustomerAccountSummary().getGlobalAccountNum());
        map.put("name", center.getDisplayName());
        map.put("externalId", center.getExternalId());
        map.put("mfiDate", center.getMfiJoiningDate().toString());
        map.put("addres", center.getAddressDto().getDisplayAddress());
        map.put("city", center.getAddressDto().getCity());
        map.put("state", center.getAddressDto().getState());
        map.put("country", center.getAddressDto().getCountry());
        map.put("postal code", center.getAddressDto().getZip());
        map.put("phone", center.getAddressDto().getPhoneNumber());
        return map;
    }

    private ObjectMapper createCenterMaping() {
        ObjectMapper om = new ObjectMapper();
        om.getDeserializationConfig()
                .addMixInAnnotations(CreateCenterDetailsDto.class, CenterCreationDetailMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationAddresDto.class, CreationAddresDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationFeeDto.class, CreationFeeDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationMeetingDto.class, CreationMeetingDtoMixIn.class);
        om.getJsonFactory().configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        return om;
    }

    private CenterCreationDetail createCenter(CreateCenterDetailsDto creationDetail) {
        return new CenterCreationDetail(creationDetail.getMfiJoiningDate(), creationDetail.getDisplayName(),
                creationDetail.getExternalId(), creationDetail.getAddressDto().toDto(), new Short(creationDetail
                        .getLoanOfficerId().shortValue()), new Short(creationDetail.getOfficeId().shortValue()),
                creationDetail.feeAsAccountFeeDto(creationDetail.getAccountFees()));
    }

    private void validate(CreateCenterDetailsDto creationDetail) throws ParamValidationException {
        validateCenterData(creationDetail);
        validateMeeting(creationDetail);
    }

    private void validateCenterData(CreateCenterDetailsDto creationDetail) throws ParamValidationException {
        if (null == creationDetail.getMeeting()) {
            throw new ParamValidationException(ErrorMessage.INVALID_MEETING);
        }
    }

    private void validateMeeting(CreateCenterDetailsDto creationDetail) throws ParamValidationException {
        if (null == creationDetail.getDisplayName()) {
            throw new ParamValidationException(ErrorMessage.INVALID_DISPLAY_NAME);
        }
        if (null == creationDetail.getLoanOfficerId()) {
            throw new ParamValidationException(ErrorMessage.INVALID_LOAN_OFFICER_ID);
        }
        if (null == creationDetail.getOfficeId()) {
            throw new ParamValidationException(ErrorMessage.INVALID_OFFICE_ID);
        }
        if (null == creationDetail.getMfiJoiningDate()) {
            throw new ParamValidationException(ErrorMessage.INVALID_MFI_DATE);
        }
    }

}

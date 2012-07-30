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
import org.mifos.application.servicefacade.CreateGroupCreationDetailDto;
import org.mifos.application.servicefacade.CreationAddresDto;
import org.mifos.application.servicefacade.CreationFeeDto;
import org.mifos.application.servicefacade.CreationMeetingDto;
import org.mifos.application.servicefacade.GroupServiceFacade;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.CustomerChargesDetailsDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.GroupCreationDetail;
import org.mifos.dto.screen.GroupInformationDto;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreateGroupCreationDetailDtoMixIn;
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
public class GroupRESTController {

    @Autowired
    private GroupServiceFacade groupServiceFacade;

    @Autowired
    private CenterServiceFacade centerServiceFacade;

    @Autowired
    private CustomerDao customerDao;

    @RequestMapping(value = "group/num-{globalCustNum}", method = RequestMethod.GET)
    public @ResponseBody
    GroupInformationDto getGroupByNumber(@PathVariable String globalCustNum) {
        return groupServiceFacade.getGroupInformationDto(globalCustNum);
    }

    @RequestMapping(value = "group/num-{globalCustNum}/charges", method = RequestMethod.GET)
    public @ResponseBody
    CustomerChargesDetailsDto getGroupChargesByNumber(@PathVariable String globalCustNum) {
        GroupBO groupBO = customerDao.findGroupBySystemId(globalCustNum);

        CustomerChargesDetailsDto groupCharges = centerServiceFacade.retrieveChargesDetails(groupBO.getCustomerId());
        groupCharges.addActivities(centerServiceFacade.retrieveRecentActivities(groupBO.getCustomerId(), 3));

        return groupCharges;
    }

    @RequestMapping(value = "group/create", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> createGroup(@RequestBody String request) throws Throwable {
        ObjectMapper om = createGroupMapping();

        CreateGroupCreationDetailDto creationDetail = null;
        MeetingBO meetingBO = null;

        try {
            creationDetail = om.readValue(request, CreateGroupCreationDetailDto.class);
        } catch (JsonMappingException e) {
            throw e.getCause();
        }
        validate(creationDetail);

        meetingBO = (MeetingBO) creationDetail.getMeeting().toBO();

        GroupCreationDetail group = createGroup(creationDetail);
        CustomerDetailsDto groupDetails = groupServiceFacade.createNewGroup(group, meetingBO.toDto());
        GroupInformationDto groupInfo = groupServiceFacade.getGroupInformationDto(groupDetails.getGlobalCustNum());

        Map<String, String> map = new HashMap<String, String>();
        map.put("status", "success");
        map.put("globalCusNum", groupInfo.getGroupDisplay().getGlobalCustNum());
        map.put("accountNum", groupInfo.getCustomerAccountSummary().getGlobalAccountNum());
        map.put("address", groupInfo.getAddress().getDisplayAddress());
        map.put("city", groupInfo.getAddress().getCity());
        map.put("state", groupInfo.getAddress().getState());
        map.put("country", groupInfo.getAddress().getCountry());
        map.put("postal code", groupInfo.getAddress().getZip());
        map.put("phone", groupInfo.getAddress().getPhoneNumber());
        map.put("dispalyName", groupInfo.getGroupDisplay().getDisplayName());
        map.put("externalId", groupInfo.getGroupDisplay().getExternalId());
        map.put("loanOfficer", groupInfo.getGroupDisplay().getLoanOfficerName());
        return map;
    }

    private ObjectMapper createGroupMapping() {
        ObjectMapper om = new ObjectMapper();
        om.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        om.getDeserializationConfig().addMixInAnnotations(CreateGroupCreationDetailDto.class,
                CreateGroupCreationDetailDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationAddresDto.class, CreationAddresDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationFeeDto.class, CreationFeeDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationMeetingDto.class, CreationMeetingDtoMixIn.class);
        om.getJsonFactory().configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        return om;
    }

    private GroupCreationDetail createGroup(CreateGroupCreationDetailDto creationDetail) {
        return new GroupCreationDetail(creationDetail.getDisplayName(), creationDetail.getExternalId(), creationDetail
                .getAddressDto().toDto(), creationDetail.getLoanOfficerId(),
                creationDetail.feeAsAccountFeeDto(creationDetail.getFeesToApply()), creationDetail.getCustomerStatus(),
                creationDetail.isTrained(), creationDetail.getTrainedOn(), creationDetail.getParentSystemId(),
                creationDetail.getOfficeId(), creationDetail.getMfiJoiningDate(), creationDetail.getActivationDate());
    }

    private void validate(CreateGroupCreationDetailDto creationDetail) throws ParamValidationException {
        validateMeeting(creationDetail);
        validateGroupData(creationDetail);
    }

    private void validateMeeting(CreateGroupCreationDetailDto creationDetail) throws ParamValidationException {
        if (null == creationDetail.getMeeting() && null == creationDetail.getParentSystemId()) {
            throw new ParamValidationException(ErrorMessage.INVALID_MEETING);
        }
    }

    private void validateGroupData(CreateGroupCreationDetailDto creationDetail) throws ParamValidationException {
        if (null == creationDetail.getDisplayName()) {
            throw new ParamValidationException(ErrorMessage.INVALID_DISPLAY_NAME);
        }
        if (null == creationDetail.getLoanOfficerId()) {
            throw new ParamValidationException(ErrorMessage.INVALID_LOAN_OFFICER_ID);
        }
        if (null == creationDetail.getOfficeId()) {
            throw new ParamValidationException(ErrorMessage.INVALID_OFFICE_ID);
        }
    }

}

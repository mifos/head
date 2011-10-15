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

package org.mifos.customers.struts.uihelpers;

import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerPositionOtherDto;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class has got helper functions which could be called from jsp as part of
 * jsp2.0 specifications.
 */
public class CustomerUIHelperFn {

    private static final Logger logger = LoggerFactory.getLogger(CustomerUIHelperFn.class);

    public CustomerUIHelperFn() {
        super();

    }

    public static String getClientPosition(Object customerPositions, Object customer) {
        logger.debug("Inside UI helper function getClientPositions");
        StringBuilder stringBuilder = new StringBuilder();
        if (customerPositions != null && customer != null) {
            logger.debug("Iterating over customerPositions list");
            List<CustomerPositionOtherDto> customerPositionList = (List<CustomerPositionOtherDto>) customerPositions;
            String positionNames[] = new String[customerPositionList.size()];
            int i = 0;
            for (CustomerPositionOtherDto customerPosition : customerPositionList) {
                CustomerDetailDto customerDetails = (CustomerDetailDto) customer;
                if (null != customerPosition && customerPosition.getCustomerId() != null
                        && customerDetails != null) {
                    if (customerPosition.getCustomerId().equals((customerDetails).getCustomerId())) {
                        String posName = customerPosition.getPositionName();
                        logger.debug("The position name is " + posName);
                        positionNames[i] = posName;
                        i++;
                    }
                }
            }
            for (; i < positionNames.length; i++) {
                positionNames[i] = null;
            }
            stringBuilder.append("(");
            for (int j = 0; j < positionNames.length; j++) {
                if (positionNames[j] != null && positionNames[j] != "") {
                    stringBuilder.append(positionNames[j]);
                }
                if (j + 1 < positionNames.length && positionNames[j + 1] != null && positionNames[j + 1] != "") {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append(")");
        }
        if (stringBuilder.toString().equals("()")) {
            return "";
        }
        return stringBuilder.toString();
    }

    public static String getMeetingSchedule(Object meeting, Object userContext) {
        if (meeting instanceof MeetingBO) {
            return new MeetingHelper().getMessage((MeetingBO) meeting, (UserContext) userContext);
        }
        return null;
    }

}

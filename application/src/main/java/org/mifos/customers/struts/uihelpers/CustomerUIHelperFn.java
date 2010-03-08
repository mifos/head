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

package org.mifos.customers.struts.uihelpers;

import java.util.Set;

import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerPositionEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.security.util.UserContext;

/**
 * This class has got helper functions which could be called from jsp as part of
 * jsp2.0 specifications.
 */
public class CustomerUIHelperFn {

    public CustomerUIHelperFn() {
        super();

    }

    public static String getClientPosition(Object customerPositions, Object customer) {
        MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER).debug("Inside UI helper function getClientPositions");
        StringBuilder stringBuilder = new StringBuilder();
        if (customerPositions != null && customer != null) {
            MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER).debug("Iterating over customerPositions list");
            Set<CustomerPositionEntity> customerPositionList = (Set<CustomerPositionEntity>) customerPositions;
            String positionNames[] = new String[customerPositionList.size()];
            int i = 0;
            for (CustomerPositionEntity customerPositionEntity : customerPositionList) {
                CustomerBO customerBO = (CustomerBO) customer;
                if (null != customerPositionEntity && customerPositionEntity.getCustomer() != null
                        && customerBO != null) {
                    if (customerPositionEntity.getCustomer().getCustomerId().equals((customerBO).getCustomerId())) {
                        String posName = customerPositionEntity.getPosition().getName();
                        MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER)
                                .debug("The position name is " + posName);
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
        } else {
            return stringBuilder.toString();
        }
    }

    public static String getMeetingSchedule(Object meeting, Object userContext) {
        if (meeting instanceof MeetingBO) {
            return meeting != null ? new MeetingHelper().getMessage((MeetingBO) meeting, (UserContext) userContext)
                    : null;
        } else {
            return null;
        }
    }

    public static String getUpdatedMeetingSchedule(Object meeting, Object userContext) {
        if (meeting instanceof CustomerMeetingEntity) {
            return meeting != null ? new MeetingHelper().getUpdatedMeetingScheduleMessage(
                    (CustomerMeetingEntity) meeting, (UserContext) userContext) : null;
        } else {
            return null;
        }
    }
}

/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.dto.domain.HolidayDetails;

public class HolidayAssembler {

    public HolidayDetails translateHolidayFormBeanToDto(HolidayFormBean formBean) {
        Date fromDate = new DateTime().withDate(Integer.parseInt(formBean.getFromYear()), formBean.getFromMonth(), formBean.getFromDay()).toDate();
        Date thruDate = null;
        if (formBean.getToDay() != null) {
            thruDate = new DateTime().withDate(Integer.parseInt(formBean.getToYear()), formBean.getToMonth(), formBean.getToDay()).toDate();
        }

        return new HolidayDetails(formBean.getName(), fromDate, thruDate, Short.valueOf(formBean.getRepaymentRuleId()));
    }

    public List<Short> convertToIds(String commaDelimetedList) {

        List<Short> branchIdList = new ArrayList<Short>();
        String[] branchIds = commaDelimetedList.split(",");
        for (String id : branchIds) {
            branchIdList.add(Short.valueOf(id));
        }

        return branchIdList;
    }
}
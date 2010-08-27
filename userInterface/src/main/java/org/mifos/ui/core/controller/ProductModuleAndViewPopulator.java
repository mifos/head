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

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.web.servlet.ModelAndView;

public class ProductModuleAndViewPopulator {

    public void populateProductDetails(GeneralProductBean bean, ModelAndView modelAndView) {
        String categoryName = bean.getCategoryOptions().get(bean.getSelectedCategory());

        DateTime startDate = new DateTime().withDate(Integer.parseInt(bean.getStartDateYear()), bean.getStartDateMonth(), bean.getStartDateDay());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDateFormatted = format.format(startDate.toDate());

        String endDateFormatted = "";
        if (StringUtils.isNotBlank(bean.getEndDateYear())) {
            DateTime endDate = new DateTime().withDate(Integer.parseInt(bean.getEndDateYear()), bean.getEndDateMonth(), bean.getEndDateDay());
            endDateFormatted = format.format(endDate.toDate());
        }

        String applicableTo = bean.getApplicableForOptions().get(bean.getSelectedApplicableFor());

        modelAndView.addObject("categoryName", categoryName);
        modelAndView.addObject("startDateFormatted", startDateFormatted);
        modelAndView.addObject("endDateFormatted", endDateFormatted);
        modelAndView.addObject("applicableTo", applicableTo);
    }

}

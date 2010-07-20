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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings( { "PMD.SystemPrintln", "PMD.SingularField" })
@Controller
public class GenericController extends AbstractController {

    @Override
    @RequestMapping(value={"/accessDenied.ftl","/pageNotFound.ftl","/ping.ftl","/cheetah.css.ftl","/gazelle.css.ftl","/adminHome.ftl","/maincss.css","/screen.css","/maincss.css.ftl","/screen.css.ftl","/admin.ftl","/viewProductCategories.ftl","/viewFunds.ftl","/defineLabels.ftl","/defineLookupOptions.ftl","/viewChecklists.ftl","/viewEditCheckLists.ftl","/viewEditSavingsProduct.ftl","/viewEditLoanProduct.ftl","/viewOffices.ftl","/viewHolidays.ftl","/viewSystemUsers.ftl","/viewAdditionalFields.ftl","/viewReportsTemplates.ftl","/viewReportsCategory.ftl","/manageRolesAndPermissions.ftl","/defineAdditionalFields.ftl","/defineNewCategory.ftl","/defineNewChecklist.ftl","/defineNewHolidays.ftl","/defineNewOffice.ftl","/defineNewSystemUser.ftl","/defineSavingsProduct.ftl","/redoLoansDisbursal.ftl"})
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)  {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("request", request);
            Map<String, Object> status = new HashMap<String, Object>();
            List<String> errorMessages = new ArrayList<String>();
            status.put("errorMessages", errorMessages);
            ModelAndView modelAndView = new ModelAndView(getPageToDisplay(request), "model", model);
            modelAndView.addObject("status", status);
            return modelAndView;
    }

    public String getPageToDisplay(HttpServletRequest request) {
        return request.getRequestURI().replaceFirst("/mifos.*/","").replace(".ftl", "");
    }
}

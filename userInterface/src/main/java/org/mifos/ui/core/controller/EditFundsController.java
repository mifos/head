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

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.mifos.accounts.fund.servicefacade.FundDto;
import org.mifos.accounts.fund.servicefacade.FundServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/editFunds")
@SessionAttributes("formBean")
public class EditFundsController {

    private static final String REDIRECT_TO_VIEW_FUNDS = "redirect:/viewFunds.ftl";
    private static final String TO_FUND_PREVIEW = "fundPreview";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE = "Cancel";

    @Autowired
    private FundServiceFacade fundServiceFacade;

    protected EditFundsController() {
        // spring autowiring
    }

    public EditFundsController(final FundServiceFacade fundServiceFacade) {
        this.fundServiceFacade = fundServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("fundEdit", "fundEdit.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public FundFormBean showFund(HttpServletRequest request) {
        Integer code = Integer.parseInt(request.getParameter("fundId"));

        FundDto fundDto = fundServiceFacade.getFund(code.shortValue());
        FundFormBean formBean = new FundFormBean();
        formBean.setCode(fundDto.getCode());
        formBean.setId(fundDto.getId());
        formBean.setName(fundDto.getName());
        return formBean;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("formBean") FundFormBean formBean, BindingResult result, SessionStatus status) {
        ModelAndView modelAndView = new ModelAndView(REDIRECT_TO_VIEW_FUNDS);
        if (CANCEL_PARAM_VALUE.equals(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_VIEW_FUNDS);
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView.setViewName("editFunds");
        } else {
            modelAndView.setViewName(TO_FUND_PREVIEW);
            modelAndView.addObject("formBean", formBean);
        }
        return modelAndView;
    }
}

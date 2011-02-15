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

package org.mifos.ui.core.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.fund.servicefacade.FundCodeDto;
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
@RequestMapping("/defineNewFund")
@SessionAttributes("formBean")
public class DefineNewFundController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private FundServiceFacade fundServiceFacade;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showPopulatedForm() {

        ModelAndView modelAndView = new ModelAndView();

        FundFormBean bean = new FundFormBean();
        modelAndView.setViewName("defineNewFund");
        modelAndView.addObject("formBean", bean);

        Map<String, String> codeMap = retrieveFundCodeOptionMap();
        modelAndView.addObject("code", codeMap);

        return modelAndView;
    }

    private Map<String, String> retrieveFundCodeOptionMap() {
        List<FundCodeDto> codeList = this.fundServiceFacade.getFundCodes();
        Map<String, String> codeMap = new LinkedHashMap<String, String>();
        for (FundCodeDto fundCode : codeList) {
            codeMap.put(fundCode.getId(), fundCode.getValue());
        }
        return codeMap;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView showPreview(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("formBean") @Valid FundFormBean formBean, BindingResult result, SessionStatus status) {

        ModelAndView modelAndView = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);
        if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_ADMIN_SCREEN);
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView.setViewName("defineNewFund");
            Map<String, String> codeMap = retrieveFundCodeOptionMap();
            modelAndView.addObject("code", codeMap);
        } else {
            modelAndView.setViewName("newFundPreview");
            Map<String, String> codeMap = retrieveFundCodeOptionMap();
            formBean.setCodeValue(codeMap.get(formBean.getCodeId()));
            modelAndView.addObject("formBean", formBean);
        }
        return modelAndView;
    }
}
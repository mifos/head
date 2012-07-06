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

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.fund.servicefacade.FundCodeDto;
import org.mifos.accounts.fund.servicefacade.FundDto;
import org.mifos.accounts.fund.servicefacade.FundServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.mifos.service.BusinessRuleException;

@Controller
@RequestMapping("/newFundPreview")
@SessionAttributes("formBean")
public class NewFundPreviewController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String REDIRECT_TO_VIEW_FUNDS = "redirect:/viewFunds.ftl";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";

    @Autowired
    private FundServiceFacade fundServiceFacade;

    protected NewFundPreviewController() {
        // spring auto wiring
    }

    public NewFundPreviewController(final FundServiceFacade fundServiceFacade) {
        this.fundServiceFacade = fundServiceFacade;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
            @RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("formBean") FundFormBean formBean,
            BindingResult result, SessionStatus status) {

        ModelAndView mav = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);

        if (StringUtils.isNotBlank(edit)) {
            mav = new ModelAndView("editFunds");
            mav.addObject("formBean", formBean);
            mav.addObject("previewView", "newFundPreview");
        } else if (StringUtils.isNotBlank(cancel)) {
            mav = new ModelAndView(REDIRECT_TO_VIEW_FUNDS);
            status.setComplete();
        } else if (result.hasErrors()) {
            mav = new ModelAndView("newFundPreview");
        } else {
            FundCodeDto codeDto=new FundCodeDto();
            codeDto.setId(formBean.getCodeId());
            codeDto.setValue(formBean.getCodeValue());
            FundDto fundDto=new FundDto();
            fundDto.setCode(codeDto);
            fundDto.setId(formBean.getId());
            fundDto.setName(formBean.getName());
            try{
                this.fundServiceFacade.createFund(fundDto);
                status.setComplete();
            } catch(BusinessRuleException e){
                ObjectError error = new ObjectError("formBean", new String[] { e.getMessageKey() }, new Object[] {},  "default: ");
                result.addError(error);
                mav.setViewName("newFundPreview");
                mav.addObject("formBean", formBean);
            }
        }
        return mav;
    }
}

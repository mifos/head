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
import org.mifos.accounts.penalty.servicefacade.PenaltyServiceFacade;
import org.mifos.dto.domain.PenaltyFormDto;
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
@RequestMapping("/newPenaltyPreview")
@SessionAttributes("formBean")
public class NewPenaltyPreviewController {
    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String REDIRECT_TO_VIEW_PENALTIES = "redirect:/viewPenalties.ftl";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";
    
    @Autowired
    private PenaltyServiceFacade penaltyServiceFacade;
    
    protected NewPenaltyPreviewController() {
        //spring autowiring
    }
    
    public NewPenaltyPreviewController(PenaltyServiceFacade penaltyServiceFacade) {
        this.penaltyServiceFacade = penaltyServiceFacade;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
            @RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("formBean") PenaltyFormBean formBean,
            BindingResult result, SessionStatus status) {

        ModelAndView modelAndView = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);

        if (StringUtils.isNotBlank(edit)) {
            modelAndView = new ModelAndView("defineNewPenalty");
            modelAndView.addObject("formBean", formBean);
            modelAndView.addObject("param", this.penaltyServiceFacade.getPenaltyParameters());
        } else if (StringUtils.isNotBlank(cancel)) {
            modelAndView = new ModelAndView(REDIRECT_TO_VIEW_PENALTIES);
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView = new ModelAndView("newPenaltyPreview");
        } else {
            boolean ratePenalty = StringUtils.isBlank(formBean.getAmount());
            Short currencyId = null;
            Double rate = null;
            Short penaltyFormula = null;
            Integer duration = null;
            
            if(ratePenalty) {
                rate = Double.valueOf(formBean.getRate());
                penaltyFormula = Short.valueOf(formBean.getFormulaId());
            }
            
            if(StringUtils.isNotBlank(formBean.getDuration())) {
                duration = Integer.valueOf(formBean.getDuration());
            }
            
            if(StringUtils.isNotBlank(formBean.getCurrencyId())) {
                currencyId = Short.valueOf(formBean.getCurrencyId());
            }
            
            PenaltyFormDto dto = new PenaltyFormDto();
            dto.setCategoryType(Short.valueOf(formBean.getCategoryTypeId()));
            dto.setPenaltyPeriod(Short.valueOf(formBean.getPeriodTypeId()));
            dto.setPenaltyFrequency(Short.valueOf(formBean.getFrequencyId()));
            dto.setGlCode(Short.valueOf(formBean.getGlCodeId()));
            dto.setPenaltyFormula(penaltyFormula);
            dto.setPenaltyName(formBean.getName());
            dto.setRatePenalty(ratePenalty);
            dto.setCurrencyId(currencyId);
            dto.setRate(rate);
            dto.setAmount(formBean.getAmount());
            dto.setDuration(duration);
            dto.setMin(Integer.valueOf(formBean.getMin()));
            dto.setMax(Integer.valueOf(formBean.getMax()));
            
            this.penaltyServiceFacade.createPenalty(dto);
            
            status.setComplete();
        }
        return modelAndView;
    }
}

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
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.config.servicefacade.dto.AccountingConfigurationDto;
import org.mifos.dto.domain.PenaltyFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("penaltyPreview")
@SessionAttributes("formBean")
public class EditPenaltyPreviewController {
    private static final String REDIRECT_TO_VIEW_PENALTY = "redirect:/viewPenalty.ftl?penaltyId=";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";

    @Autowired
    private PenaltyServiceFacade penaltyServiceFacade;
    @Autowired
    private ConfigurationServiceFacade configurationServiceFacade;
    
    protected EditPenaltyPreviewController() {
        // spring auto wiring
    }

    public EditPenaltyPreviewController(final PenaltyServiceFacade penaltyServiceFacade, final ConfigurationServiceFacade configurationServiceFacade) {
        this.penaltyServiceFacade = penaltyServiceFacade;
        this.configurationServiceFacade = configurationServiceFacade;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
            @RequestParam(value = CANCEL_PARAM, required = false) String cancel, PenaltyFormBean formBean,
            BindingResult result, SessionStatus status) {
        ModelAndView modelAndView = new ModelAndView();
        AccountingConfigurationDto configurationDto = this.configurationServiceFacade.getAccountingConfiguration();
        modelAndView.addObject("GLCodeMode",  configurationDto.getGlCodeMode());
        
        if (StringUtils.isNotBlank(edit)) {
            modelAndView.setViewName("editPenalty");
            modelAndView.addObject("formBean", formBean);
            modelAndView.addObject("param", this.penaltyServiceFacade.getPenaltyParameters());
        } else if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_VIEW_PENALTY + formBean.getId());
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView.setViewName("penaltyPreview");
        } else {
            modelAndView.setViewName(REDIRECT_TO_VIEW_PENALTY + formBean.getId());
            
            Short id = Short.valueOf(formBean.getId());
            Short categoryType = Short.valueOf(formBean.getCategoryTypeId());
            Short penaltyStatus = Short.valueOf(formBean.getStatusId());
            Short penaltyFrequency = Short.valueOf(formBean.getFrequencyId());
            Short glCode = Short.valueOf(formBean.getGlCodeId());
            boolean ratePenalty = StringUtils.isBlank(formBean.getAmount());
            Short currencyId = null;
            Double rate = null;
            Short penaltyFormula = null;
            Integer duration = null;
            Short penaltyPeriod = 3;
            
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
            
            if(StringUtils.isNotBlank(formBean.getPeriodTypeId())) {
                penaltyPeriod = Short.valueOf(formBean.getPeriodTypeId());
            }
            
            Double min = Double.valueOf(formBean.getMin());
            Double max = Double.valueOf(formBean.getMax());
            
            PenaltyFormDto dto = new PenaltyFormDto();
            dto.setId(id);
            dto.setCategoryType(categoryType);
            dto.setPenaltyStatus(penaltyStatus);
            dto.setPenaltyPeriod(penaltyPeriod);
            dto.setPenaltyFrequency(penaltyFrequency);
            dto.setGlCode(glCode);
            dto.setPenaltyFormula(penaltyFormula);
            dto.setPenaltyName(formBean.getName());
            dto.setRatePenalty(ratePenalty);
            dto.setCurrencyId(currencyId);
            dto.setRate(rate);
            dto.setAmount(formBean.getAmount());
            dto.setDuration(duration);
            dto.setMin(min);
            dto.setMax(max);
            
            this.penaltyServiceFacade.updatePenalty(dto);
            
            status.setComplete();
        }
        
        return modelAndView;
    }
}

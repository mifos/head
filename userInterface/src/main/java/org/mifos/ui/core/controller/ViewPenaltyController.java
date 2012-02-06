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

import org.mifos.accounts.penalty.servicefacade.PenaltyServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/viewPenalty.ftl")
@SessionAttributes("formBean")
public class ViewPenaltyController {
    @Autowired
    private PenaltyServiceFacade penaltyServiceFacade;
    
    protected ViewPenaltyController() {
        //for spring autowiring
    }

    public ViewPenaltyController(final PenaltyServiceFacade penaltyServiceFacade) {
        this.penaltyServiceFacade = penaltyServiceFacade;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showPenalty(@RequestParam Integer penaltyId) {
        ModelAndView modelAndView = new ModelAndView("viewPenalty");
        
        modelAndView.addObject("penalty", this.penaltyServiceFacade.getPenalty(penaltyId));
        
        return modelAndView;
    }
}

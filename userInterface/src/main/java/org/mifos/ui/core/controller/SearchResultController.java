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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.application.servicefacade.CustomerSearchServiceFacade;
import org.mifos.core.MifosException;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.screen.CustomerHierarchyDto;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;  
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("PMD")
@Controller
@RequestMapping("/searchResult")
@SessionAttributes("customerSearch")
public class SearchResultController {
    private static final int pageSize = 10;

    @Autowired
    private CustomerSearchServiceFacade customerSearchServiceFacade;

    @Autowired
    private OfficeServiceFacade officeServiceFacade;

    @ModelAttribute("customerSearch")
    public CustomerSearchFormBean populateForm(){
        return new CustomerSearchFormBean();
    }
    
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView showSearchResults(HttpServletRequest request, SitePreference sitePreference,
            @ModelAttribute("customerSearch") @Valid CustomerSearchFormBean customerSearchFormBean, BindingResult result) throws MifosException {
        Device currentDevice = DeviceUtils.getCurrentDevice(request);
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomerHierarchyDto customerHierarchyDto = null;
        
        List<OfficeDto> officeDtoList = officeServiceFacade.retrieveActiveBranchesUnderUser((short) user.getUserId());
        Map<String, String> officesMap = new HashMap<String, String>();
        for (OfficeDto officeDto : officeDtoList){
            officesMap.put(officeDto.getId().toString(), officeDto.getName());
        }     
        customerSearchFormBean.setOffices(officesMap);
        
        ModelAndView modelAndView = new ModelAndView("m_searchResult");
        if (currentDevice.isMobile()) {
            modelAndView = new ModelAndView("m_searchResult");
        }
        
        if ( result.hasErrors() ){
            return modelAndView; 
        }        
        
        int currentPage = 0;
        if (request.getParameter("currentPage") != null) {
            currentPage = new Integer(request.getParameter("currentPage")).intValue();
        }
        
        modelAndView.addObject("customerSearch", customerSearchFormBean);
        
        customerHierarchyDto = customerSearchServiceFacade.search(customerSearchFormBean.getSearchString(), customerSearchFormBean.getOfficeId(), currentPage*this.pageSize, this.pageSize);

        boolean prevPageAvailable = false;
        if (currentPage > 0) {
            prevPageAvailable = true;
        }
        boolean nextPageAvailable = false;
        if ( customerHierarchyDto.getSize()/this.pageSize > 0 && customerHierarchyDto.getSize()/this.pageSize >= currentPage + 1  ) {
            nextPageAvailable = true;
        }

        modelAndView.addObject("isPrevPageAvailable", prevPageAvailable);
        modelAndView.addObject("isNextPageAvailable", nextPageAvailable);
        modelAndView.addObject("currentPage", currentPage);
        modelAndView.addObject("pageSize", this.pageSize);

        modelAndView.addObject("customerHierarchy", customerHierarchyDto);

        return modelAndView;
    }
    
    @InitBinder("customerSearch")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new CustomerSearchFormValidator());
    }

}

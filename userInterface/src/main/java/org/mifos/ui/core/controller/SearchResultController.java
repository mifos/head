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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.application.servicefacade.CustomerSearchServiceFacade;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.CustomerHierarchyDto;
import org.mifos.dto.screen.CustomerStatusDetailDto;
import org.mifos.dto.screen.SearchFiltersDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.MifosUser;
import org.mifos.ui.core.controller.util.helpers.SitePreferenceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("PMD")
@Controller
@SessionAttributes("customerSearch")
public class SearchResultController {
    private static final int PAGE_SIZE = 10;

    @Autowired
    private CustomerSearchServiceFacade customerSearchServiceFacade;

    @Autowired
    private OfficeServiceFacade officeServiceFacade;

    @Autowired
    private ConfigurationServiceFacade configurationServiceFacade;

    @Autowired
    private ClientServiceFacade clientServiceFacade;

    private final SitePreferenceHelper sitePreferenceHelper = new SitePreferenceHelper();

    @ModelAttribute("customerSearch")
    public CustomerSearchFormBean populateForm() {
        return new CustomerSearchFormBean();
    }

    @RequestMapping(value = "/legacySearchResult", method = { RequestMethod.POST, RequestMethod.GET })
    public ModelAndView legacyShowSearchResults(HttpServletRequest request,
            @ModelAttribute("customerSearch") @Valid CustomerSearchFormBean customerSearchFormBean, BindingResult result) throws PersistenceException {
    	ModelAndView modelAndView = new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "legacySearchResult", request);
        
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomerHierarchyDto customerHierarchyDto = null;

        List<OfficeDto> officeDtoList = officeServiceFacade.retrieveActiveBranchesUnderUser((short) user.getUserId());
        Map<String, String> officesMap = new HashMap<String, String>();
        for (OfficeDto officeDto : officeDtoList) {
            officesMap.put(officeDto.getId().toString(), officeDto.getName());
        }
        customerSearchFormBean.setOffices(officesMap);
        
        boolean isCenterHierarchyExists = configurationServiceFacade.getBooleanConfig("ClientRules.CenterHierarchyExists");
        modelAndView.addObject("isCenterHierarchyExists", isCenterHierarchyExists );
        
        HashMap<String, ArrayList<CustomerStatusDetailDto>> customerStates = new HashMap<String, ArrayList<CustomerStatusDetailDto>>();
        customerStates.putAll(customerSearchServiceFacade.getAvailibleCustomerStates());
        modelAndView.addObject("availibleCustomerStates", customerStates);
        
        List<ValueListElement> availibleClientGenders = clientServiceFacade.getClientGenders();
        modelAndView.addObject("availibleClientGenders", availibleClientGenders);

        if (result.hasErrors()) {
            return modelAndView;
        }

        int currentPage = 0;
        if (request.getParameter("currentPage") != null) {
            currentPage = new Integer(request.getParameter("currentPage")).intValue();
        }

        modelAndView.addObject("customerSearch", customerSearchFormBean);

        if (customerSearchFormBean.getFilters() == null) {
            customerSearchFormBean.setFilters(new SearchFiltersDto());
        }
        customerHierarchyDto = customerSearchServiceFacade.search(customerSearchFormBean.getSearchString(),
                customerSearchFormBean.getOfficeId(), currentPage * PAGE_SIZE, PAGE_SIZE, customerSearchFormBean.getFilters());

        boolean prevPageAvailable = false;
        if (currentPage > 0) {
            prevPageAvailable = true;
        }
        boolean nextPageAvailable = false;
        if (customerHierarchyDto.getSize() / PAGE_SIZE > 0
                && customerHierarchyDto.getSize() / PAGE_SIZE >= currentPage + 1) {
            nextPageAvailable = true;
        }

        modelAndView.addObject("isPrevPageAvailable", prevPageAvailable);
        modelAndView.addObject("isNextPageAvailable", nextPageAvailable);
        modelAndView.addObject("currentPage", currentPage);
        modelAndView.addObject("pageSize", PAGE_SIZE);

        modelAndView.addObject("customerHierarchy", customerHierarchyDto);

        return modelAndView;
    }
    
    @RequestMapping(value = "/searchResult", method = { RequestMethod.POST, RequestMethod.GET } )
    public ModelAndView showSearchResults(HttpServletRequest request, @ModelAttribute("customerSearch") @Valid CustomerSearchFormBean customerSearchFormBean, 
    		BindingResult result) throws PersistenceException{
    	ModelAndView modelAndView = new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "searchResult", request);
        
        // mobile search result view doesn't use ajax search
		if (sitePreferenceHelper.isMobile(request)) {
			return legacyShowSearchResults(request, customerSearchFormBean, result);
		}

    	MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<OfficeDto> officeDtoList = officeServiceFacade.retrieveActiveBranchesUnderUser((short) user.getUserId());
		
		Map<String, String> officesMap = new HashMap<String, String>();
		for (OfficeDto officeDto : officeDtoList) {
			officesMap.put(officeDto.getId().toString(), officeDto.getName());
		}
        customerSearchFormBean.setOffices(officesMap);
        
		modelAndView.addObject("customerSearch", customerSearchFormBean);
		
        boolean isCenterHierarchyExists = configurationServiceFacade.getBooleanConfig("ClientRules.CenterHierarchyExists");
        modelAndView.addObject("isCenterHierarchyExists", isCenterHierarchyExists );
        
        HashMap<String, ArrayList<CustomerStatusDetailDto>> customerStates = new HashMap<String, ArrayList<CustomerStatusDetailDto>>();
        customerStates.putAll(customerSearchServiceFacade.getAvailibleCustomerStates());
        modelAndView.addObject("availibleCustomerStates", customerStates);
        
        List<ValueListElement> availibleClientGenders = clientServiceFacade.getClientGenders();
        modelAndView.addObject("availibleClientGenders", availibleClientGenders);
	
    	if (result.hasErrors()) {
    		return modelAndView;
        }
		
    	CustomerHierarchyDto customerHierarchyDto = new CustomerHierarchyDto();
    	
    	if ( customerSearchFormBean.getSearchString() != null && !customerSearchFormBean.getSearchString().isEmpty() ){
    		customerHierarchyDto = customerSearchServiceFacade.search(customerSearchFormBean.getSearchString(),
                    customerSearchFormBean.getOfficeId(), 0, 10, customerSearchFormBean.getFilters());
    	}
    	
    	modelAndView.addObject("customerHierarchy", customerHierarchyDto);
    	modelAndView.addObject("startIndex", 0);
    	
    	return modelAndView;
    }
    
    @RequestMapping(value = "/searchResultAjaxData", method = RequestMethod.GET )
    public ModelAndView getSearchResultAjaxData(HttpServletResponse response, @ModelAttribute("customerSearch") CustomerSearchFormBean customerSearchFormBean, 
    		@RequestParam(required=false) String sEcho, @RequestParam Integer iDisplayStart, @RequestParam Integer iDisplayLength) throws PersistenceException{
    	ModelAndView modelAndView = new ModelAndView("searchResultAjaxData");
    	
    	CustomerHierarchyDto customerHierarchyDto = new CustomerHierarchyDto();    
    	
    	boolean isCenterHierarchyExists = configurationServiceFacade.getBooleanConfig("ClientRules.CenterHierarchyExists");
    	modelAndView.addObject("isCenterHierarchyExists", isCenterHierarchyExists );
    	
    	HashMap<String, ArrayList<CustomerStatusDetailDto>> customerStates = new HashMap<String, ArrayList<CustomerStatusDetailDto>>();
        customerStates.putAll(customerSearchServiceFacade.getAvailibleCustomerStates());
        modelAndView.addObject("availibleCustomerStates", customerStates);
        
        List<ValueListElement> availibleClientGenders = clientServiceFacade.getClientGenders();
        modelAndView.addObject("availibleClientGenders", availibleClientGenders);
    	
    	if ( customerSearchFormBean.getSearchString() != null && !customerSearchFormBean.getSearchString().isEmpty() ){
    		customerHierarchyDto = customerSearchServiceFacade.search(customerSearchFormBean.getSearchString(),
                    customerSearchFormBean.getOfficeId(), iDisplayStart, iDisplayLength, customerSearchFormBean.getFilters());
    	}
    	
    	if ( sEcho != null ){
    		modelAndView.addObject("sEcho", sEcho);    		
    	}
    	modelAndView.addObject("customerHierarchy", customerHierarchyDto);
    	modelAndView.addObject("iDisplayStart", iDisplayStart);
    	modelAndView.addObject("iDisplayLength", iDisplayLength);
    	
    	return modelAndView;
    }
    
    @InitBinder("customerSearch")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new CustomerSearchFormValidator());
    }

}

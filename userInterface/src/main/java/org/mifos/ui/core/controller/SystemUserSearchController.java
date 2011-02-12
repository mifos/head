/*
 * Copyright Grameen Foundation USA
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.dto.domain.UserSearchDto;
import org.mifos.dto.screen.SystemUserSearchResultsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/viewSystemUsers")
@SessionAttributes(value = {"searchResults", "pagedResults"})
public class SystemUserSearchController {

    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;

    protected SystemUserSearchController(){
        //for spring autowiring
    }

    public SystemUserSearchController(PersonnelServiceFacade personnelServiceFacade) {
        this.personnelServiceFacade = personnelServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displaySystemUsers(ModelMap model) {

        ModelAndView mav = new ModelAndView("viewSystemUsers");

        SystemUserSearchFormBean formBean = (SystemUserSearchFormBean) model.get("searchResults");
        SystemUserSearchResultsDto result = (SystemUserSearchResultsDto) model.get("pagedResults");
        if (result == null) {
            List<UserDetailDto> pagedUserDetails = new ArrayList<UserDetailDto>();
            result = new SystemUserSearchResultsDto(0, 0, 0, 0, pagedUserDetails);
            formBean = new SystemUserSearchFormBean();
        }

        mav.addObject("searchResults", formBean);
        mav.addObject("pagedResults", result);
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processSearch(@RequestParam(required=false, value="next") String next,
            @RequestParam(required=false, value="previous") String previous,
            @RequestParam(required=false, value="searchbutton") String newSearch,
            @RequestParam(required=false, value="lastSearch") String lastSearchTerm,
            @RequestParam(required=true, value="lastPage") Integer lastPage,
            @ModelAttribute("searchResults") SystemUserSearchFormBean searchResultsFormBean) {

        int startingPage = lastPage;
        if (StringUtils.isNotBlank(next)) {
            startingPage++;
            searchResultsFormBean.setSearch(lastSearchTerm);
        } else if (StringUtils.isNotBlank(previous)) {
            startingPage--;
            searchResultsFormBean.setSearch(lastSearchTerm);
        } else if (StringUtils.isNotBlank(newSearch)) {
            startingPage = 1;
        }

        if (startingPage <= 0) {
            startingPage = 1;
        }

        UserSearchDto searchDto = new UserSearchDto(searchResultsFormBean.getSearch(), startingPage, 10);

        SystemUserSearchResultsDto dto = this.personnelServiceFacade.searchUser(searchDto);

        ModelAndView mav = new ModelAndView("redirect:/viewSystemUsers.ftl");
        mav.addObject("searchResults", searchResultsFormBean);
        mav.addObject("pagedResults", dto);

        return mav;
    }
}
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
package org.mifos.platform.rest.ui.controller;

import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.security.MifosUser;
import org.mifos.config.ClientRules;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.persistence.CustomerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PersonnelRESTController {

    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PersonnelDao personnelDao;

    @RequestMapping(value = "personnel/id-current", method = RequestMethod.GET)
    public final @ResponseBody
    PersonnelInformationDto getCurrentPersonnel() {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return personnelServiceFacade.getPersonnelInformationDto((long)user.getUserId(), "");
    }

    @RequestMapping(value = "personnel/clients/id-current", method = RequestMethod.GET)
    public final @ResponseBody
    CustomerHierarchy getCustomersUnderPersonnel() {
        PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(getCurrentPersonnel().getPersonnelId());

        CustomerHierarchy hierarchy = new CustomerHierarchy();

        if (ClientRules.getCenterHierarchyExists()) {
            for (CustomerDetailDto center : this.customerDao.findActiveCentersUnderUser(loanOfficer)) {
                CenterDescription centerDescription = new CenterDescription();
                centerDescription.setId(center.getCustomerId());
                centerDescription.setDisplayName(center.getDisplayName());
                centerDescription.setGlobalCustNum(center.getGlobalCustNum());
                centerDescription.setSearchId(center.getSearchId());
                hierarchy.getCenters().add(centerDescription);
            }
        }

        allGroups:
        for (CustomerDetailDto group : this.customerDao.findGroupsUnderUser(loanOfficer)) {
            GroupDescription groupDescription = new GroupDescription();
            groupDescription.setId(group.getCustomerId());
            groupDescription.setDisplayName(group.getDisplayName());
            groupDescription.setGlobalCustNum(group.getGlobalCustNum());
            groupDescription.setSearchId(group.getSearchId());

            for (ClientBO client : this.customerDao.findActiveClientsUnderParent(group.getSearchId(), loanOfficer.getOffice().getOfficeId())) {
                ClientDescription clientDescription = new ClientDescription();
                clientDescription.setId(client.getCustomerId());
                clientDescription.setDisplayName(client.getDisplayName());
                clientDescription.setGlobalCustNum(client.getGlobalCustNum());
                clientDescription.setSearchId(client.getSearchId());
                groupDescription.getClients().add(clientDescription);
            }

            for (CenterDescription center : hierarchy.getCenters()) {
                if (group.getSearchId().startsWith(center.getSearchId())) {
                    center.getGroups().add(groupDescription);
                    continue allGroups;
                }
            }
            hierarchy.getGroups().add(groupDescription);
        }

        for (ClientBO client : this.customerDao.findActiveClientsWithoutGroupForLoanOfficer(loanOfficer.getPersonnelId(), loanOfficer.getOffice().getOfficeId())) {
            ClientDescription clientDescription = new ClientDescription();
                clientDescription.setId(client.getCustomerId());
                clientDescription.setDisplayName(client.getDisplayName());
                clientDescription.setGlobalCustNum(client.getGlobalCustNum());
                clientDescription.setSearchId(client.getSearchId());
                hierarchy.getClients().add(clientDescription);
        }

        return hierarchy;
    }

    static class CustomerHierarchy {
        private List<CenterDescription> centers = new ArrayList<CenterDescription>();
        private List<GroupDescription> groups = new ArrayList<GroupDescription>();
        private List<ClientDescription> clients = new ArrayList<ClientDescription>();

        public List<CenterDescription> getCenters() {
            return centers;
        }

        public List<GroupDescription> getGroups() {
            return groups;
        }

        public List<ClientDescription> getClients() {
            return clients;
        }
    }

    static class CenterDescription {
        private Integer id;
        private String displayName;
        private String globalCustNum;
        private String searchId;
        private List<GroupDescription> groups = new ArrayList<GroupDescription>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getGlobalCustNum() {
            return globalCustNum;
        }

        public void setGlobalCustNum(String globalCustNum) {
            this.globalCustNum = globalCustNum;
        }

        public String getSearchId() {
            return searchId;
        }

        public void setSearchId(String searchId) {
            this.searchId = searchId;
        }

        public List<GroupDescription> getGroups() {
            return groups;
        }
    }

    static class GroupDescription {
        private Integer id;
        private String displayName;
        private String globalCustNum;
        private String searchId;
        private List<ClientDescription> clients = new ArrayList<ClientDescription>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getGlobalCustNum() {
            return globalCustNum;
        }

        public void setGlobalCustNum(String globalCustNum) {
            this.globalCustNum = globalCustNum;
        }

        public String getSearchId() {
            return searchId;
        }

        public void setSearchId(String searchId) {
            this.searchId = searchId;
        }

        public List<ClientDescription> getClients() {
            return clients;
        }
    }

    static class ClientDescription {
        private Integer id;
        private String displayName;
        private String globalCustNum;
        private String searchId;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getGlobalCustNum() {
            return globalCustNum;
        }

        public void setGlobalCustNum(String globalCustNum) {
            this.globalCustNum = globalCustNum;
        }

        public String getSearchId() {
            return searchId;
        }

        public void setSearchId(String searchId) {
            this.searchId = searchId;
        }
    }
}

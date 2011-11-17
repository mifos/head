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

package org.mifos.customers.struts.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Days;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.calendar.CalendarUtils;
import org.mifos.config.ClientRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.struts.actionforms.CustSearchActionForm;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerSearchConstants;
import org.mifos.dto.domain.CenterDescriptionDto;
import org.mifos.dto.domain.ClientDescriptionDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerHierarchyDto;
import org.mifos.dto.domain.GroupDescriptionDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SearchUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;

public class CustSearchAction extends SearchAction {

    @TransactionDemarcate(joinToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;


        boolean isCenterHierarchyExist = ClientRules.getCenterHierarchyExists();

        if (StringUtils.isNotBlank(actionForm.getLoanOfficerId())) {

            Short loanOfficerId = Short.valueOf(actionForm.getLoanOfficerId());
            List<CustomerDetailDto> customerList = this.centerServiceFacade.retrieveCustomersUnderUser(loanOfficerId);

            SessionUtils.setCollectionAttribute(CustomerSearchConstants.CUSTOMERLIST, customerList, request);
            SessionUtils.setAttribute("GrpHierExists", isCenterHierarchyExist, request);
            SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDLOANOFFICER, request);
        }

        UserContext userContext = getUserContext(request);
        Short userBranchId = userContext.getBranchId();
        String officeName = retrieveOfficeName(actionForm, userBranchId);

        SessionUtils.setAttribute("isCenterHierarchyExists", isCenterHierarchyExist, request);
        SessionUtils.setAttribute(CustomerSearchConstants.OFFICE, officeName, request);
        SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDNONLOANOFFICER, request);
        return mapping.findForward(CustomerSearchConstants.LOADFORWARDLOANOFFICER_SUCCESS);
    }

    private String retrieveOfficeName(CustSearchActionForm actionForm, Short userBranchId) {
        String officeName;
        if (StringUtils.isNotBlank(actionForm.getOfficeId())) {
            Short officeId = Short.valueOf(actionForm.getOfficeId());
            officeName = this.centerServiceFacade.retrieveOfficeName(officeId);
        } else {
            officeName = this.centerServiceFacade.retrieveOfficeName(userBranchId);
        }
        return officeName;
    }

    @TransactionDemarcate(conditionToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;

        if (StringUtils.isNotBlank(actionForm.getOfficeId())) {
            List<PersonnelBO> personnelList = legacyPersonnelDao.getActiveLoanOfficersUnderOffice(getShortValue(actionForm.getOfficeId()));
            SessionUtils.setCollectionAttribute(CustomerSearchConstants.LOANOFFICERSLIST, personnelList, request);
        }

        UserContext userContext = getUserContext(request);
        Short userBranchId = userContext.getBranchId();
        String officeName = retrieveOfficeName(actionForm, userBranchId);

        SessionUtils.setAttribute(CustomerSearchConstants.OFFICE, officeName, request);
        SessionUtils.setAttribute("isCenterHierarchyExists", ClientRules.getCenterHierarchyExists(), request);
        SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDNONLOANOFFICER, request);

        return mapping.findForward(CustomerSearchConstants.LOADFORWARDNONLOANOFFICER_SUCCESS);
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward loadAllBranches(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        actionForm.setOfficeId("0");
        UserContext userContext = getUserContext(request);
        SessionUtils.setAttribute("isCenterHierarchyExists", ClientRules.getCenterHierarchyExists(), request);

        loadMasterData(userContext.getId(), request, actionForm);
        return mapping.findForward(CustomerSearchConstants.LOADALLBRANCHES_SUCCESS);

    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward getHomePage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        actionForm.setSearchString(null);

        cleanUpSearch(request);
        UserContext userContext = getUserContext(request);
        UserDetailDto userDetails = this.centerServiceFacade.retrieveUsersDetails(userContext.getId());     
        SessionUtils.setAttribute("isCenterHierarchyExists", ClientRules.getCenterHierarchyExists(), request);
        loadMasterData(userContext.getId(), request, actionForm);
        if (userDetails.isLoanOfficer()) {
            loadLoanOfficerCustomersHierarchyForSelectedDay(userContext.getId(), request, actionForm);
        }
        return mapping.findForward(CustomerConstants.GETHOMEPAGE_SUCCESS);
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward loadSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        actionForm.setSearchString(null);
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
        cleanUpSearch(request);
        return mapping.findForward(ActionForwards.loadSearch_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward loadMainSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String forward = null;
        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        actionForm.setSearchString(null);
        actionForm.setOfficeId("0");
        cleanUpSearch(request);
        UserContext userContext = getUserContext(request);
        SessionUtils.setAttribute("isCenterHierarchyExists", ClientRules.getCenterHierarchyExists(), request);

        forward = loadMasterData(userContext.getId(), request, actionForm);
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward mainSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        Short officeId = getShortValue(actionForm.getOfficeId());
        String searchString = actionForm.getSearchString();
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        super.search(mapping, form, request, response);
        if (searchString == null || searchString.equals("")) {

            ActionErrors errors = new ActionErrors();

            errors.add(CustomerSearchConstants.NAMEMANDATORYEXCEPTION, new ActionMessage(
                    CustomerSearchConstants.NAMEMANDATORYEXCEPTION));

            request.setAttribute(Globals.ERROR_KEY, errors);
            return mapping.findForward(ActionForwards.mainSearch_success.toString());
        }

        if (officeId != null && officeId != 0) {
            addSeachValues(searchString, officeId.toString(), new OfficePersistence().getOffice(officeId).getOfficeName(), request);
        } else {
            addSeachValues(searchString, officeId.toString(), new OfficePersistence().getOffice(userContext.getBranchId()).getOfficeName(), request);
        }
        searchString = SearchUtils.normalizeSearchString(searchString);
        if (searchString.equals("")) {
            throw new CustomerException(CustomerSearchConstants.NAMEMANDATORYEXCEPTION);
        }
        QueryResult customerSearchResult = new CustomerPersistence().search(searchString, officeId, userContext.getId(), userContext.getBranchId());
        SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, customerSearchResult, request);
        return mapping.findForward(ActionForwards.mainSearch_success.toString());

    }

    @TransactionDemarcate(conditionToken = true)
    public ActionForward getOfficeHomePage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Short loggedUserLevel = getUserContext(request).getLevelId();
        if (loggedUserLevel.equals(PersonnelLevel.LOAN_OFFICER.getValue())) {
            return loadMainSearch(mapping, form, request, response);
        }
        return preview(mapping, form, request, response);
    }

    private String loadMasterData(Short userId, HttpServletRequest request, CustSearchActionForm form) throws Exception {
        UserDetailDto userDetails = this.centerServiceFacade.retrieveUsersDetails(userId);
        PersonnelBO personnel = legacyPersonnelDao.getPersonnel(userId);
        SessionUtils.setAttribute(CustomerSearchConstants.OFFICE, userDetails.getOfficeName(), request);
        if (userDetails.isLoanOfficer()) {
            SessionUtils.setAttribute("isLoanOfficer", true, request);
            return loadLoanOfficer(personnel, request);
        }
        SessionUtils.setAttribute("isLoanOfficer", false, request);
        return loadNonLoanOfficer(personnel, request, form);

    }

    private String loadLoanOfficer(PersonnelBO personnel, HttpServletRequest request) throws Exception {
        /*
         * John W - this method is called by loadMasterData. loadMasterData is called by getHomePage, loadAllBranches
         * and loadMainSearch (which in turn is called by getOfficeHomePage). I couldn't find out where in the user
         * interface these public methods were used. I didn't delete because I wasn't sure.
         */

        // see centerServiceFacade.retrieveCustomersUnderUser(loanOfficerId) for replacing below code.
        List<CustomerBO> customerList = null;
        
        boolean isCenterHierarchyExist = ClientRules.getCenterHierarchyExists();

        if (isCenterHierarchyExist) {
            customerList = new CustomerPersistence().getActiveCentersUnderUser(personnel);
        } else {
            customerList = new CustomerPersistence().getGroupsUnderUser(personnel);
        }
        
        SessionUtils.setCollectionAttribute(CustomerSearchConstants.CUSTOMERLIST, customerList, request);
        SessionUtils.setAttribute("GrpHierExists", isCenterHierarchyExist, request);
        SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDLOANOFFICER, request);

        return CustomerSearchConstants.LOADFORWARDLOANOFFICER_SUCCESS;
    }

    /**
     * Retrieve meetings for loan officer for selected day.
     * @param personnel
     * @param request
     * @return
     * @throws Exception
     */
    private String loadLoanOfficerCustomersHierarchyForSelectedDay(Short userId, HttpServletRequest request, CustSearchActionForm form) throws Exception {
        PersonnelBO personnel = legacyPersonnelDao.getPersonnel(userId);
        
        CustomerHierarchyDto hierarchy = new CustomerHierarchyDto();
        List<String> nearestDates = new ArrayList<String>();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        // Yesterday as current date because upcoming meetings should include current day
        Date currentDate = new DateTime().minusDays(1).toDate();
        Date selectedDate;
        
        DateTime nextDate = new DateTime();
        for (int i = 0; i < 7; i++){
            nearestDates.add(formatter.format(nextDate.toDate()));
            nextDate = nextDate.plusDays(1);
        }
        
        if ( form.getSelectedDateOption() != null ){
           selectedDate = formatter.parse(form.getSelectedDateOption());
        } else {
           selectedDate = new Date();
        }

        if (ClientRules.getCenterHierarchyExists()) {
            for (CustomerBO customer : new CustomerPersistence().getActiveCentersUnderUser(personnel)){
                DateTime nextMeeting = new DateTime(customer.getCustomerMeetingValue().getNextScheduleDateAfterRecurrenceWithoutAdjustment(currentDate));
                if ( Days.daysBetween(new DateTime(selectedDate), nextMeeting).getDays() == 0 ){
                    CenterDescriptionDto centerDescription = new CenterDescriptionDto();
                    centerDescription.setId(customer.getCustomerId());
                    centerDescription.setDisplayName(customer.getDisplayName());
                    centerDescription.setGlobalCustNum(customer.getGlobalCustNum());
                    centerDescription.setSearchId(customer.getSearchId());
                    hierarchy.getCenters().add(centerDescription);
                }
            }
        }
        
        allGroups:
        for (CustomerBO group : new CustomerPersistence().getGroupsUnderUser(personnel)){       
            DateTime nextMeeting = new DateTime(group.getCustomerMeetingValue().getNextScheduleDateAfterRecurrenceWithoutAdjustment(currentDate));
            if ( Days.daysBetween(new DateTime(selectedDate), nextMeeting).getDays() == 0 ){     
                GroupDescriptionDto groupDescription = new GroupDescriptionDto();
                groupDescription.setId(group.getCustomerId());
                groupDescription.setDisplayName(group.getDisplayName());
                groupDescription.setGlobalCustNum(group.getGlobalCustNum());
                groupDescription.setSearchId(group.getSearchId());

                for (ClientBO client : this.customerDao.findActiveClientsUnderParent(group.getSearchId(), personnel.getOffice().getOfficeId())) {
                    ClientDescriptionDto clientDescription = new ClientDescriptionDto();
                    clientDescription.setId(client.getCustomerId());
                    clientDescription.setDisplayName(client.getDisplayName());
                    clientDescription.setGlobalCustNum(client.getGlobalCustNum());
                    clientDescription.setSearchId(client.getSearchId());
                    groupDescription.getClients().add(clientDescription);
                }

                for (CenterDescriptionDto center : hierarchy.getCenters()) {
                    if (group.getSearchId().startsWith(center.getSearchId())) {
                        center.getGroups().add(groupDescription);
                        continue allGroups;
                    }
                }
                hierarchy.getGroups().add(groupDescription);
            }
        }
        
        SessionUtils.setCollectionAttribute("nearestDates", nearestDates, request);
        SessionUtils.setAttribute("hierarchy", hierarchy, request);
        return CustomerSearchConstants.LOADFORWARDLOANOFFICER_SUCCESS;
    }
    
    private String loadNonLoanOfficer(PersonnelBO personnel, HttpServletRequest request, CustSearchActionForm form)
            throws Exception {
        if (personnel.getOffice().getOfficeLevel().equals(OfficeLevel.BRANCHOFFICE)) {
            List<PersonnelBO> personnelList = legacyPersonnelDao.getActiveLoanOfficersUnderOffice(personnel.getOffice().getOfficeId());
            SessionUtils.setCollectionAttribute(CustomerSearchConstants.LOANOFFICERSLIST, personnelList, request);
            SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDNONLOANOFFICER, request);
            form.setOfficeId(personnel.getOffice().getOfficeId().toString());

            return CustomerSearchConstants.LOADFORWARDNONLOANOFFICER_SUCCESS;
        }

        SessionUtils.setCollectionAttribute(CustomerSearchConstants.OFFICESLIST, new OfficePersistence().getActiveBranchesUnderUser(personnel.getOfficeSearchId()), request);
        SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDNONBRANCHOFFICE, request);

        return CustomerSearchConstants.LOADFORWARDOFFICE_SUCCESS;
    }

    /**
     * FIXME: KEITHW - When replacing search functionality for customers with spring/ftl implementation,
     * find cleaner way of implementing search that returns a non domain related class (data only object)
     */
    @Override
    @TransactionDemarcate(joinToken = true)
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
        ActionForward actionForward = super.search(mapping, form, request, response);

        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        UserContext userContext = getUserContext(request);

        String searchString = actionForm.getSearchString();
        if (searchString == null) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        String officeName = this.centerServiceFacade.retrieveOfficeName(userContext.getBranchId());
        addSeachValues(searchString, userContext.getBranchId().toString(), officeName, request);
        searchString = SearchUtils.normalizeSearchString(searchString);

        if (StringUtils.isBlank(searchString)) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        if (actionForm.getInput() != null && actionForm.getInput().equals("loan")) {
            QueryResult groupClients = new CustomerPersistence().searchGroupClient(searchString, userContext.getId());
            SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, groupClients, request);
        } else if (actionForm.getInput() != null && actionForm.getInput().equals("savings")) {
            QueryResult customerForSavings = new CustomerPersistence().searchCustForSavings(searchString, userContext.getId());
            SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, customerForSavings, request);
        }
        return actionForward;
    }
}
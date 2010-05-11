/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.application.holiday.struts.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.service.HolidayBusinessService;
import org.mifos.application.holiday.persistence.HolidayDetails;
import org.mifos.application.holiday.persistence.HolidayServiceFacadeWebTier;
import org.mifos.application.holiday.struts.actionforms.HolidayActionForm;
import org.mifos.application.holiday.util.helpers.HolidayConstants;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

import edu.emory.mathcs.backport.java.util.Arrays;

public class HolidayAction extends BaseAction {

    @Override
    protected BusinessService getService() throws ServiceException {
        return new HolidayBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("holidayAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("getHolidays", SecurityConstants.VIEW);
        security.allow("addHoliday", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());

        doCleanUp(request);

        request.getSession().setAttribute("HolidayActionForm", null);

        request.getSession().setAttribute(HolidayConstants.REPAYMENTRULETYPES, getRepaymentRuleTypes());

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    public ActionForward getHolidays(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());

        // call method to set list of holidays in session
        setHolidayListInSession(request, userContext);

        return mapping.findForward("view_organizational_holidays");
    }

    private List<HolidayBO> getHolidays(int year, int localeId) throws Exception {
        return getHolidayBizService().getHolidays(year);
    }

    private Map<Short, String> getRepaymentRuleTypes() throws Exception {
        List<RepaymentRuleTypes> repaymentRuleTypes = Arrays.asList(RepaymentRuleTypes.values());
        Map<Short, String> repaymentRuleTypesMap = new TreeMap<Short, String>();
        for (RepaymentRuleTypes repaymentRule : repaymentRuleTypes) {
            repaymentRuleTypesMap.put(repaymentRule.getValue(), repaymentRule.getName());
        }
        return repaymentRuleTypesMap;
    }

    public ActionForward addHoliday(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(HolidayConstants.REPAYMENTRULETYPES, getRepaymentRuleTypes());
        return mapping.findForward(ActionForwards.load_success.toString());// "create_office_holiday");
    }

    private HolidayBusinessService getHolidayBizService() {
        return new HolidayBusinessService();
    }

    private void doCleanUp(HttpServletRequest request) {
        SessionUtils.setAttribute(HolidayConstants.HOLIDAY_ACTIONFORM, null, request.getSession());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());

        request.getSession().setAttribute(HolidayConstants.REPAYMENTRULETYPES, getRepaymentRuleTypes());

        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());

        request.getSession().setAttribute(HolidayConstants.REPAYMENTRULETYPES, getRepaymentRuleTypes());

        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelCreate_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());

        // call method to set list of holidays in session
        setHolidayListInSession(request, userContext);

        return mapping.findForward(ActionForwards.get_success.toString());
    }

    private List<HolidayBO> getDistinctYears() throws Exception {
        List returnValues = getHolidayBizService().getDistinctYears();
        return returnValues;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getEditStates(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward managePreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // HolidayActionForm holidayActionForm = (HolidayActionForm) form;

        return mapping.findForward(ActionForwards.managepreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward managePrevious(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.manageprevious_success.toString());
    }

    // @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        HolidayActionForm holidayActionForm = (HolidayActionForm) form;
        RepaymentRuleTypes repaymentRuleType = RepaymentRuleTypes.fromShort(new Short(holidayActionForm
                .getRepaymentRuleId()));
        HolidayDetails holidayDetails = new HolidayDetails(holidayActionForm.getHolidayName(), holidayActionForm
                .getFromDate(), holidayActionForm.getThruDate(), repaymentRuleType);
        List<Short> officeIds = new LinkedList<Short>();
        // TODO as of now all holidays are associated to head office
        // this should be a list of office id(s) in the action form
        // when branch level holiday is implemented.
        officeIds.add(new Short((short) 1));
        // TODO HolidayServiceFacadeWebTier and OfficePersistence to be injected
        new HolidayServiceFacadeWebTier(new OfficePersistence()).createHoliday(holidayDetails, officeIds);

        if (null != request.getParameter(Constants.CURRENTFLOWKEY)) {
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
        }
        FlowManager flowManager = new FlowManager();
        Flow flow = new Flow();
        flow.addObjectToSession(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
        flowManager.addFLow(Constants.CURRENTFLOWKEY, flow, this.clazz.getName());
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));

        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelManage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelEdit_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {

        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    private void setHolidayListInSession(HttpServletRequest request, UserContext userContext) throws Exception {
        List years = getDistinctYears();
        Set distinctYears = null;
        if (years != null && years.size() != 0) {
            List temp = new ArrayList();
            Iterator iter = years.iterator();

            while (iter.hasNext()) {
                String date = iter.next().toString();
                date = date.substring(0, 4);
                temp.add(date);
            }

            // distinctYears = new HashSet(temp);
            distinctYears = new TreeSet(temp);
        }

        int yearGroupingCount = 1;
        if (distinctYears != null && distinctYears.size() != 0) {
            Iterator iter = distinctYears.iterator();
            while (iter.hasNext()) {
                String year = (String) iter.next();
                int intYear = Integer.parseInt(year);
                SessionUtils.setCollectionAttribute(HolidayConstants.HOLIDAY_LIST + yearGroupingCount, getHolidays(
                        intYear, userContext.getLocaleId()), request);
                request.getSession().setAttribute(HolidayConstants.YEAR + yearGroupingCount, intYear);
                yearGroupingCount++;
            }
        }
        request.getSession().setAttribute(HolidayConstants.NO_OF_YEARS, yearGroupingCount - 1);
    }

}

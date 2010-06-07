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

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.holiday.business.service.HolidayBusinessService;
import org.mifos.application.holiday.persistence.HolidayDetails;
import org.mifos.application.holiday.persistence.HolidayServiceFacadeWebTier;
import org.mifos.application.holiday.persistence.OfficeHoliday;
import org.mifos.application.holiday.struts.actionforms.HolidayActionForm;
import org.mifos.application.holiday.util.helpers.HolidayConstants;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.customers.office.business.service.OfficeFacade;
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
    protected boolean skipActionFormToBusinessObjectConversion(@SuppressWarnings("unused") String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("holidayAction");
        security.allow("load", SecurityConstants.CAN_DEFINE_HOLIDAY);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("getHolidays", SecurityConstants.VIEW);
        security.allow("addHoliday", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("officeHierarchy", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.CAN_DEFINE_HOLIDAY);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        doCleanUp(request);
        request.getSession().setAttribute("HolidayActionForm", null);
        request.getSession().setAttribute(HolidayConstants.REPAYMENTRULETYPES, getRepaymentRuleTypes());
        return mapping.findForward(ActionForwards.load_success.toString());
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
        ((HolidayActionForm)form).setSelectedOfficeIds("");
        request.getSession().setAttribute(HolidayConstants.REPAYMENTRULETYPES, getRepaymentRuleTypes());
        return mapping.findForward(ActionForwards.load_success.toString());// "create_office_holiday");
    }

    private void doCleanUp(HttpServletRequest request) {
        SessionUtils.setAttribute(HolidayConstants.HOLIDAY_ACTIONFORM, null, request.getSession());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(HolidayConstants.REPAYMENTRULETYPES, getRepaymentRuleTypes());
        String selectedOfficeIds = ((HolidayActionForm) form).getSelectedOfficeIds();
        request.getSession().setAttribute(HolidayConstants.SELECTED_OFFICE_NAMES,
                new OfficeFacade(new OfficeBusinessService()).topLevelOfficeNames(selectedOfficeIds));
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
    public ActionForward get(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        int count = 1;
        Map<String, List<OfficeHoliday>> holidaysByYear = holidayServiceFacade.holidaysByYear();

        Set<String> distinctYears = holidaysByYear.keySet();
        for (String year : distinctYears) {
            request.getSession().setAttribute(HolidayConstants.HOLIDAY_LIST + count, holidaysByYear.get(year));
            request.getSession().setAttribute(HolidayConstants.YEAR + count, year);
            count++;
        }
        request.getSession().setAttribute(HolidayConstants.NO_OF_YEARS, count - 1);
        return mapping.findForward(ActionForwards.get_success.toString());
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
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        HolidayActionForm holidayActionForm = (HolidayActionForm) form;

        RepaymentRuleTypes repaymentRuleType = RepaymentRuleTypes.fromShort(Short.valueOf(holidayActionForm.getRepaymentRuleId()));
        HolidayDetails holidayDetails = assembleHolidayDetails(holidayActionForm, repaymentRuleType);

        List<Short> officeIds = new LinkedList<Short>();
        String[] selectedOfficeIds = holidayActionForm.getSelectedOfficeIds().split(",");
        for (String selectedOfficeId : selectedOfficeIds) {
            officeIds.add(Short.valueOf(selectedOfficeId));
        }

        this.holidayServiceFacade.createHoliday(holidayDetails, officeIds);

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

    private HolidayDetails assembleHolidayDetails(HolidayActionForm holidayActionForm,
            RepaymentRuleTypes repaymentRuleType) {
        return new HolidayDetails(holidayActionForm.getHolidayName(), holidayActionForm.getFromDate(), holidayActionForm.getThruDate(), repaymentRuleType);
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


    public ActionForward officeHierarchy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        httpservletresponse.setContentType("application/json");
        PrintWriter out = httpservletresponse.getWriter();
        out.println(new OfficeFacade(new OfficeBusinessService()).headOfficeHierarchy().toJSONString());
        out.flush();
        return null;
    }
}

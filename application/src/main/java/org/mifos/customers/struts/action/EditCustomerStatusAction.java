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

import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.CustomerStatusFlagEntity;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.struts.actionforms.EditCustomerStatusActionForm;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.screen.CustomerStatusDetailDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditCustomerStatusAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(EditCustomerStatusAction.class);

    public EditCustomerStatusAction() {
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In EditCustomerStatusAction:load()");
        EditCustomerStatusActionForm editCustomerStatusActionForm = (EditCustomerStatusActionForm) form;
        editCustomerStatusActionForm.clear();

        UserContext userContext = getUserContext(request);

        Integer customerId = editCustomerStatusActionForm.getCustomerIdValue();
        CustomerBO customer = this.customerDao.findCustomerById(customerId);
        customer.setUserContext(userContext);
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customer, request);

        List<CustomerStatusEntity> statusList = new ArrayList<CustomerStatusEntity>();
        switch(customer.getLevel()) {
        case CENTER:
            this.centerServiceFacade.initializeCenterStates(customer.getGlobalCustNum());
            statusList = AccountStateMachines.getInstance().getCenterStatusList(customer.getCustomerStatus());
            editCustomerStatusActionForm.setInput("center");
            break;
        case GROUP:
            this.centerServiceFacade.initializeGroupStates(customer.getGlobalCustNum());
            statusList = AccountStateMachines.getInstance().getGroupStatusList(customer.getCustomerStatus());
            editCustomerStatusActionForm.setInput("group");
            break;
        case CLIENT:
            this.centerServiceFacade.initializeClientStates(customer.getGlobalCustNum());
            statusList = AccountStateMachines.getInstance().getClientStatusList(customer.getCustomerStatus());
            editCustomerStatusActionForm.setInput("client");
            break;
        default:
            break;
        };

        for (CustomerStatusEntity customerStatusEntity : statusList) {
            for (CustomerStatusFlagEntity flag : customerStatusEntity.getFlagSet()) {
                String statusMessageText = ApplicationContextProvider.getBean(MessageLookup.class).lookup(flag.getLookUpValue().getPropertiesKey());
                flag.setStatusFlagMessageText(statusMessageText);
            }
        }

        editCustomerStatusActionForm.setLevelId(customer.getCustomerLevel().getId().toString());
        editCustomerStatusActionForm.setCurrentStatusId(customer.getCustomerStatus().getId().toString());
        editCustomerStatusActionForm.setGlobalAccountNum(customer.getGlobalCustNum());
        editCustomerStatusActionForm.setCustomerName(customer.getDisplayName());

        SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_LIST, statusList, request);
        return mapping.findForward(ActionForwards.loadStatus_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In EditCustomerStatusAction:preview()");

        CustomerBO customerBO = (CustomerBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        EditCustomerStatusActionForm statusActionForm = (EditCustomerStatusActionForm) form;
        statusActionForm.setCommentDate(DateUtils.getCurrentDate(getUserContext(request).getPreferredLocale()));

        if (StringUtils.isNotBlank(statusActionForm.getNewStatusId())) {
            CustomerStatusDetailDto customerStatusDetail = this.centerServiceFacade.retrieveCustomerStatusDetails(statusActionForm.getNewStatusIdValue(), statusActionForm.getFlagIdValue(), customerBO.getLevel().getValue());
            SessionUtils.setAttribute(SavingsConstants.NEW_STATUS_NAME, customerStatusDetail.getStatusName(), request);
            SessionUtils.setAttribute(SavingsConstants.FLAG_NAME, customerStatusDetail.getFlagName(), request);
        } else {
            SessionUtils.setAttribute(SavingsConstants.NEW_STATUS_NAME, null, request);
            SessionUtils.setAttribute(SavingsConstants.FLAG_NAME, null, request);
        }


        Short newStatusId = statusActionForm.getNewStatusIdValue();
        Short customerLevelId = statusActionForm.getLevelIdValue();
        List<CustomerCheckListBO> checklist = new CustomerPersistence().getStatusChecklist(newStatusId, customerLevelId);
        SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_CHECK_LIST, checklist, request);

        if (statusActionForm.getNewStatusIdValue().equals(CustomerStatus.CLIENT_CLOSED.getValue())) {
            return createClientQuestionnaire.fetchAppliedQuestions(mapping, statusActionForm, request, ActionForwards.previewStatus_success);
        }
        return mapping.findForward(ActionForwards.previewStatus_success.toString());
    }

    private QuestionnaireFlowAdapter createClientQuestionnaire = new QuestionnaireFlowAdapter("Close", "Client",
            ActionForwards.previewStatus_success, "clientCustAction.do?method=get", new DefaultQuestionnaireServiceFacadeLocator());

    @TransactionDemarcate(joinToken = true)
    public ActionForward previousStatus(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In EditCustomerStatusAction:previous()");
        return mapping.findForward(ActionForwards.previousStatus_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updateStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        EditCustomerStatusActionForm editStatusActionForm = (EditCustomerStatusActionForm) form;
        CustomerBO customerBOInSession = (CustomerBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        try {
            this.centerServiceFacade.updateCustomerStatus(customerBOInSession.getCustomerId(), customerBOInSession.getVersionNo(),
                                                          editStatusActionForm.getFlagId(), editStatusActionForm.getNewStatusId(),
                                                          editStatusActionForm.getNotes());
            createClientQuestionnaire.saveResponses(request, editStatusActionForm, customerBOInSession.getCustomerId());
        } catch (BusinessRuleException e) {
            throw new ApplicationException(e.getMessageKey(), e);
        }

        return mapping.findForward(getDetailAccountPage(form));
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelStatus(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In EditCustomerStatusAction:cancel()");
        return mapping.findForward(getDetailAccountPage(form));
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) {
        logger.debug("In EditCustomerStatusAction:validate()");
        String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
        String forward = null;
        if (method != null) {
            if (method.equals(Methods.preview.toString())) {
                forward = ActionForwards.preview_failure.toString();
            } else if (method.equals(Methods.load.toString())) {
                forward = getDetailAccountPage(form);
            } else if (method.equals(Methods.update.toString())) {
                forward = ActionForwards.update_failure.toString();
            } else if (method.equals(Methods.previewStatus.toString())) {
                forward = ActionForwards.previewStatus_failure.toString();
            } else if (method.equals(Methods.loadStatus.toString())) {
                forward = getDetailAccountPage(form);
            } else if (method.equals(Methods.updateStatus.toString())) {
                forward = ActionForwards.updateStatus_failure.toString();
            }
        }
        return mapping.findForward(forward);
    }

    private String getDetailAccountPage(ActionForm form) {
        EditCustomerStatusActionForm editStatusActionForm = (EditCustomerStatusActionForm) form;
        String input = editStatusActionForm.getInput();
        String forward = null;
        if (input.equals("center")) {
            forward = ActionForwards.center_detail_page.toString();
        } else if (input.equals("group")) {
            forward = ActionForwards.group_detail_page.toString();
        } else if (input.equals("client")) {
            forward = ActionForwards.client_detail_page.toString();
        }
        return forward;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward captureQuestionResponses(
            final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "captureQuestionResponses");
        ActionErrors errors = createClientQuestionnaire.validateResponses(request, (EditCustomerStatusActionForm) form);
        if (errors != null && !errors.isEmpty()) {
            addErrors(request, errors);
            return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
        }
        return createClientQuestionnaire.rejoinFlow(mapping);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editQuestionResponses(
            final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "editQuestionResponses");
        return createClientQuestionnaire.editResponses(mapping, request, (EditCustomerStatusActionForm) form);
    }
}

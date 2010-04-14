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

package org.mifos.accounts.acceptedpaymenttype.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.acceptedpaymenttype.business.AcceptedPaymentType;
import org.mifos.accounts.acceptedpaymenttype.business.TransactionTypeEntity;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.acceptedpaymenttype.struts.actionform.AcceptedPaymentTypeActionForm;
import org.mifos.accounts.acceptedpaymenttype.util.helpers.AcceptedPaymentTypeConstants;
import org.mifos.accounts.acceptedpaymenttype.util.helpers.PaymentTypeData;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class AcceptedPaymentTypeAction extends BaseAction {

    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("acceptedPaymentTypeAction");
        security.allow("load", SecurityConstants.CAN_DEFINE_ACCEPTED_PAYMENT_TYPE);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("cancel", SecurityConstants.VIEW);
        return security;
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    private List<PaymentTypeData> getAllPaymentTypes(Short localeId) throws Exception {
        List<PaymentTypeData> paymentTypeList = new ArrayList<PaymentTypeData>();
        PaymentTypeData payment = null;
        Short id = 0;
        List<MasterDataEntity> paymentTypes = getMasterEntities(PaymentTypeEntity.class, localeId);
        for (MasterDataEntity masterDataEntity : paymentTypes) {
            PaymentTypeEntity paymentType = (PaymentTypeEntity) masterDataEntity;
            id = paymentType.getId();
            payment = new PaymentTypeData(id);
            payment.setName(paymentType.getName());
            paymentTypeList.add(payment);
        }

        return paymentTypeList;
    }

    private void RemoveFromInList(List<PaymentTypeData> list, Short paymentTypeId) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getId().shortValue() == paymentTypeId.shortValue()) {
                list.remove(i);
            }
        }
    }

    private void setPaymentTypesForATransaction(List<PaymentTypeData> payments, TrxnTypes transactionType,
            AcceptedPaymentTypePersistence paymentTypePersistence, HttpServletRequest request, Short localeId)
            throws Exception {

        Short transactionId = transactionType.getValue();
        List<AcceptedPaymentType> paymentTypeList = paymentTypePersistence
                .getAcceptedPaymentTypesForATransaction(transactionId);
        List<PaymentTypeData> inList = new ArrayList<PaymentTypeData>(payments);
        List<PaymentTypeData> outList = new ArrayList<PaymentTypeData>();

        PaymentTypeData data = null;
        for (AcceptedPaymentType paymentType : paymentTypeList) {
            Short paymentTypeId = paymentType.getPaymentTypeEntity().getId();
            data = new PaymentTypeData(paymentTypeId);
            data.setName(paymentType.getPaymentTypeEntity().getName());
            data.setAcceptedPaymentTypeId(paymentType.getAcceptedPaymentTypeId());
            outList.add(data);
            RemoveFromInList(inList, paymentTypeId);
        }
        if (transactionType == TrxnTypes.loan_repayment) {
            SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_REPAYMENT_LIST, inList, request);
            SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_REPAYMENT_LIST, outList, request);
        } else if (transactionType == TrxnTypes.fee) {
            SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_FEE_LIST, inList, request);
            SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_FEE_LIST, outList, request);
        } else if (transactionType == TrxnTypes.loan_disbursement) {
            SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_DISBURSEMENT_LIST, inList, request);
            SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_DISBURSEMENT_LIST, outList, request);
        } else if (transactionType == TrxnTypes.savings_deposit) {
            SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_DEPOSIT_LIST, inList, request);
            SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_DEPOSIT_LIST, outList, request);
        } else if (transactionType == TrxnTypes.savings_withdrawal) {
            SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_WITHDRAWAL_LIST, inList, request);
            SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_WITHDRAWAL_LIST, outList, request);
        } else {
            throw new Exception("Unknow account action for accepted payment type " + transactionType.toString());
        }
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside load method");
        AcceptedPaymentTypeActionForm acceptedPaymentTypeActionForm = (AcceptedPaymentTypeActionForm) form;
        acceptedPaymentTypeActionForm.clear();
        UserContext userContext = getUserContext(request);
        Short localeId = userContext.getLocaleId();
        List<PaymentTypeData> payments = getAllPaymentTypes(userContext.getLocaleId());
        // acceptedPaymentTypeActionForm.setAllPaymentTypes(payments);
        AcceptedPaymentTypePersistence paymentTypePersistence = new AcceptedPaymentTypePersistence();
        for (int i = 0; i < TrxnTypes.values().length; i++) {
            setPaymentTypesForATransaction(payments, TrxnTypes.values()[i], paymentTypePersistence, request, localeId);
        }

        logger.debug("Outside load method");
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    private boolean FindDelete(PaymentTypeData paymentType, String[] paymentTypes) {
        if (paymentTypes == null) {
            return true;
        }
        Short paymentTypeId = paymentType.getId();
        for (String paymentType2 : paymentTypes) {
            Short paymentId = Short.parseShort(paymentType2);
            if (paymentId.shortValue() == paymentTypeId.shortValue()) {
                return false;
            }
        }
        return true;
    }

    private boolean FindNew(Short paymentTypeId, List<PaymentTypeData> paymentTypes) {

        for (PaymentTypeData paymentTypeData : paymentTypes) {
            Short paymentId = paymentTypeData.getId();
            if (paymentId.shortValue() == paymentTypeId.shortValue()) {
                return false;
            }
        }
        return true;
    }

    private void Process(String[] selectedPaymentTypes, List<PaymentTypeData> outList,
            List<AcceptedPaymentType> deletedPaymentTypeList, List<AcceptedPaymentType> addedPaymentTypeList,
            AcceptedPaymentTypePersistence persistence, TrxnTypes transactionType) {
        AcceptedPaymentType acceptedPaymentType = null;
        if ((outList != null) && (outList.size() > 0)) {
            for (PaymentTypeData paymentType : outList) {
                if (FindDelete(paymentType, selectedPaymentTypes)) {
                    acceptedPaymentType = persistence.getAcceptedPaymentType(paymentType.getAcceptedPaymentTypeId());
                    deletedPaymentTypeList.add(acceptedPaymentType);
                }
            }
        }
        if (selectedPaymentTypes != null) {
            for (String selectedPaymentType : selectedPaymentTypes) {
                Short paymentTypeId = Short.parseShort(selectedPaymentType);
                if (FindNew(paymentTypeId, outList)) {
                    acceptedPaymentType = new AcceptedPaymentType();
                    PaymentTypeEntity paymentTypeEntity = new PaymentTypeEntity(paymentTypeId);
                    acceptedPaymentType.setPaymentTypeEntity(paymentTypeEntity);
                    TransactionTypeEntity transactionEntity = new TransactionTypeEntity();
                    transactionEntity.setTransactionId(transactionType.getValue());
                    acceptedPaymentType.setTransactionTypeEntity(transactionEntity);
                    addedPaymentTypeList.add(acceptedPaymentType);
                }
            }
        }
    }

    private void ProcessOneAccountActionAcceptedPaymentTypes(TrxnTypes transactionType,
            AcceptedPaymentTypeActionForm acceptedPaymentTypeActionForm,
            List<AcceptedPaymentType> deletedPaymentTypeList, List<AcceptedPaymentType> addedPaymentTypeList,
            HttpServletRequest request, AcceptedPaymentTypePersistence persistence) throws Exception {
        // new accepted payments
        String[] selectedPaymentTypes = null;
        // old accepted payments
        List<PaymentTypeData> outList = null;
        if (transactionType == TrxnTypes.fee) {
            selectedPaymentTypes = acceptedPaymentTypeActionForm.getFees();
            outList = (List<PaymentTypeData>) SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_FEE_LIST,
                    request);
        } else if (transactionType == TrxnTypes.loan_disbursement) {
            selectedPaymentTypes = acceptedPaymentTypeActionForm.getDisbursements();
            outList = (List<PaymentTypeData>) SessionUtils.getAttribute(
                    AcceptedPaymentTypeConstants.OUT_DISBURSEMENT_LIST, request);
        } else if (transactionType == TrxnTypes.loan_repayment) {
            selectedPaymentTypes = acceptedPaymentTypeActionForm.getRepayments();
            outList = (List<PaymentTypeData>) SessionUtils.getAttribute(
                    AcceptedPaymentTypeConstants.OUT_REPAYMENT_LIST, request);
        } else if (transactionType == TrxnTypes.savings_deposit) {
            selectedPaymentTypes = acceptedPaymentTypeActionForm.getDeposits();
            outList = (List<PaymentTypeData>) SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_DEPOSIT_LIST,
                    request);
        } else if (transactionType == TrxnTypes.savings_withdrawal) {
            selectedPaymentTypes = acceptedPaymentTypeActionForm.getWithdrawals();
            outList = (List<PaymentTypeData>) SessionUtils.getAttribute(
                    AcceptedPaymentTypeConstants.OUT_WITHDRAWAL_LIST, request);
        } else {
            throw new Exception("Unknow account action for accepted payment type " + transactionType.toString());
        }
        Process(selectedPaymentTypes, outList, deletedPaymentTypeList, addedPaymentTypeList, persistence,
                transactionType);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside update method");
        AcceptedPaymentTypeActionForm acceptedPaymentTypeActionForm = (AcceptedPaymentTypeActionForm) form;
        AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();

        List<AcceptedPaymentType> deletedPaymentTypeList = new ArrayList();
        List<AcceptedPaymentType> addedPaymentTypeList = new ArrayList();
        for (int i = 0; i < TrxnTypes.values().length; i++) {
            ProcessOneAccountActionAcceptedPaymentTypes(TrxnTypes.values()[i], acceptedPaymentTypeActionForm,
                    deletedPaymentTypeList, addedPaymentTypeList, request, persistence);
        }

        if (addedPaymentTypeList.size() > 0) {
            persistence.addAcceptedPaymentTypes(addedPaymentTypeList);
        }
        if (deletedPaymentTypeList.size() > 0) {
            persistence.deleteAcceptedPaymentTypes(deletedPaymentTypeList);
        }

        return mapping.findForward(ActionForwards.update_success.toString());

    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("cancel method called");
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @Override
    protected BusinessService getService() {
        return ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Configuration);
    }

}

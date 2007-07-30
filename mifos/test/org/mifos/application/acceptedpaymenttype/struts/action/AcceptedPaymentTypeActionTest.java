package org.mifos.application.acceptedpaymenttype.struts.action;



import org.mifos.application.acceptedpaymenttype.business.AcceptedPaymentType;
import org.mifos.application.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.application.acceptedpaymenttype.struts.actionform.AcceptedPaymentTypeActionForm;
import org.mifos.application.acceptedpaymenttype.struts.action.AcceptedPaymentTypeAction;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.application.acceptedpaymenttype.util.helpers.AcceptedPaymentTypeConstants;
import org.mifos.application.acceptedpaymenttype.util.helpers.PaymentTypeData;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class AcceptedPaymentTypeActionTest extends MifosMockStrutsTestCase{

	private UserContext userContext;
	private String flowKey;
	String[] EXPECTED_ALL_PAYMENT_TYPES = {"Cash", "Voucher", "Check"};
	private AccountActionTypes[] accountActionTypesForAcceptedPaymentType =
	{AccountActionTypes.FEE_REPAYMENT, AccountActionTypes.LOAN_REPAYMENT, 
			AccountActionTypes.DISBURSAL, AccountActionTypes.SAVINGS_DEPOSIT, 
			AccountActionTypes.SAVINGS_WITHDRAWAL};
	private List<PaymentTypeData> feeOutList = null;
	private List<PaymentTypeData> disbursementOutList = null;
	private List<PaymentTypeData> repaymentOutList = null;
	private List<PaymentTypeData> depositOutList = null;
	private List<PaymentTypeData> withdrawalOutList = null;
	
	
	
	
	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/acceptedpaymenttype/struts-config.xml")
				.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, AcceptedPaymentTypeAction.class);
	}
	
	private boolean FindPaymentType(List<PaymentTypeData> list, String payment)
	{
		for (PaymentTypeData paymentType : list)
		{
			if (paymentType.getName().equals(payment))
				return true;
		}
		return false;
	}

	private boolean compareLists(List<PaymentTypeData> first, String[] second, int expectedLength) {
		
		assertEquals(expectedLength, first.size());
		for (int index = 0; index < second.length; ++index) {
			assertTrue(FindPaymentType(first, second[index]));

		}
		return true;
	}
	
	private String[] TotalList(List<PaymentTypeData> inList, List<PaymentTypeData> outList)
	{
		int totalLength = 0;
		if (inList != null)
			totalLength += inList.size();
		if (outList != null)
			totalLength += outList.size();
		
		String[] totalList = new String[totalLength];
		Short index = 0;
		if (inList != null)
		{
			for (PaymentTypeData payment : inList)
				totalList[index++] = payment.getName();
		}
		if (outList != null)
		{
			for (PaymentTypeData payment : outList)
				totalList[index++] = payment.getName();
		}
		return totalList;
	}
	
	public void testLoad() throws Exception {
		setRequestPathInfo("/acceptedPaymentTypeAction.do");
		addRequestParameter("method", "load");
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		AcceptedPaymentTypeActionForm acceptedPaymentTypeActionForm = 
			(AcceptedPaymentTypeActionForm) request
				.getSession().getAttribute("acceptedpaymenttypeactionform");
		
		List<PaymentTypeData> list = acceptedPaymentTypeActionForm.getAllPaymentTypes();
		assertTrue(compareLists(list, EXPECTED_ALL_PAYMENT_TYPES, 3));	
		List<PaymentTypeData> inList = null;
		List<PaymentTypeData> outList = null;

		inList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.IN_REPAYMENT_LIST, request);
		outList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_REPAYMENT_LIST, request);
		String[] totalList = TotalList(inList, outList);
		assertTrue(compareLists(list, totalList, 3));
		
		inList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.IN_FEE_LIST, request);
		outList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_FEE_LIST, request);
		totalList = TotalList(inList, outList);
		assertTrue(compareLists(list, totalList, 3));
		
		inList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.IN_DISBURSEMENT_LIST, request);
		outList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_DISBURSEMENT_LIST, request);
		totalList = TotalList(inList, outList);
		assertTrue(compareLists(list, totalList, 3));
		
		inList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.IN_DEPOSIT_LIST, request);
		inList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_DEPOSIT_LIST, request);
		totalList = TotalList(inList, outList);
		assertTrue(compareLists(list, totalList, 3));
		
		inList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.IN_WITHDRAWAL_LIST, request);
		inList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_WITHDRAWAL_LIST, request);
		totalList = TotalList(inList, outList);
		assertTrue(compareLists(list, totalList, 3));
		
		
	}
	
	
	
	private List<PaymentTypeData> getAllPaymentTypes(Locale locale)
	{
	     List<PaymentTypeData> paymentTypeList = new ArrayList();
	     PaymentTypeData payment = null;
	     Short id = 0;
	     for (PaymentTypes paymentType : PaymentTypes.values()) {
	    	 id = paymentType.getValue();
	    	 payment = new PaymentTypeData(id);
	    	 String paymentName = MessageLookup.getInstance().lookup(paymentType, locale);
	    	 payment.setName(paymentName);
	    	 paymentTypeList.add(payment);
			}
	     
	     return paymentTypeList;
	}
	
	private String GetPaymentTypeName(Short paymentTypeId, List<PaymentTypeData> paymentTypes)
	{
		
		for (PaymentTypeData paymentTypeData : paymentTypes)
		{
			Short paymentId = paymentTypeData.getId();
			if (paymentId.shortValue() == paymentTypeId.shortValue())
				 return paymentTypeData.getName();
		}
		return "";
	}
	
	private void RemoveFromInList(List<PaymentTypeData> list, Short paymentTypeId)
	{
		for (int i=list.size()-1; i >= 0; i--) 
		{
			if (list.get(i).getId().shortValue() == paymentTypeId.shortValue())
				list.remove(i);
		}
	}
	
	private void setPaymentTypesForAnAccountAction(List<PaymentTypeData> payments, AccountActionTypes accountAction,
			AcceptedPaymentTypePersistence paymentTypePersistence, HttpServletRequest request, boolean reverse) throws Exception
	{
		
		Short accountActionId = accountAction.getValue();
		List<AcceptedPaymentType> paymentTypeList = paymentTypePersistence.getAcceptedPaymentTypesForAnAccountAction(accountActionId);
		List<PaymentTypeData> inList = new ArrayList(payments);
		List<PaymentTypeData> outList = new ArrayList();
	
		PaymentTypeData data = null;
		for (AcceptedPaymentType paymentType : paymentTypeList)
		{
			Short paymentTypeId = paymentType.getPaymentType().getId();
			data = new PaymentTypeData(paymentTypeId);
			data.setName(GetPaymentTypeName(paymentTypeId, payments));
			data.setAcceptedPaymentTypeId(paymentType.getAcceptedPaymentTypeId());
			outList.add(data);
			RemoveFromInList(inList, paymentTypeId);
		}
		if (accountAction == AccountActionTypes.LOAN_REPAYMENT)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_REPAYMENT_LIST,
					inList, request);
			if (reverse == false)
				feeOutList = outList;
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_REPAYMENT_LIST,
					outList, request);
		}
		else if (accountAction == AccountActionTypes.FEE_REPAYMENT)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_FEE_LIST,
					inList, request);
			if (reverse == false)
				repaymentOutList = outList;
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_FEE_LIST,
					outList, request);
		}
		else if (accountAction == AccountActionTypes.DISBURSAL)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_DISBURSEMENT_LIST,
					inList, request);
			if (reverse == false)
				disbursementOutList = outList;
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_DISBURSEMENT_LIST,
					outList, request);
		}
		else if (accountAction == AccountActionTypes.SAVINGS_DEPOSIT)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_DEPOSIT_LIST,
					inList, request);
			if (reverse == false)
				depositOutList = outList;
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_DEPOSIT_LIST,
					outList, request);
		}
		else if (accountAction == AccountActionTypes.SAVINGS_WITHDRAWAL)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_WITHDRAWAL_LIST,
					inList, request);
			if (reverse == false)
				withdrawalOutList = outList;
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_WITHDRAWAL_LIST,
					outList, request);
		}
		else
			throw new Exception("Unknow account action for accepted payment type " + accountAction.toString());
	}
	
	private boolean Find(Short paymentTypeId, List<PaymentTypeData> payments)
	{
		if (payments.size() == 0)
			return false;
		for (PaymentTypeData paymentTypeData : payments)
		{
			Short paymentId = paymentTypeData.getId();
			if (paymentId.shortValue() == paymentTypeId.shortValue())
				 return true;
		}
		return false;
	}
	
	
	private void AddAPayment(List<PaymentTypeData> selectedList, List<PaymentTypeData> allPayments)
	{
		for (PaymentTypeData payment: allPayments)
			if (Find(payment.getId(), selectedList) == false)
			{
				selectedList.add(payment);
				return;
			}
		 
	}
	
	private void setSelectedPaymentTypesForAnAccountActionType(AccountActionTypes accountAction,
			AcceptedPaymentTypeActionForm form )
		throws Exception
	{
		List<PaymentTypeData> selectedList = null;
		List<PaymentTypeData> allPayments = form.getAllPaymentTypes();
		
		if (accountAction == AccountActionTypes.LOAN_REPAYMENT)
		{
			selectedList = new ArrayList(repaymentOutList);
		}
		else if (accountAction == AccountActionTypes.FEE_REPAYMENT)
		{		
			selectedList = new ArrayList(feeOutList);
		}
		else if (accountAction == AccountActionTypes.DISBURSAL)
		{		
			selectedList = new ArrayList(disbursementOutList);
		}
		else if (accountAction == AccountActionTypes.SAVINGS_DEPOSIT)
		{		
			selectedList = new ArrayList(depositOutList);
		}
		else if (accountAction == AccountActionTypes.SAVINGS_WITHDRAWAL)
		{		
			selectedList = new ArrayList(withdrawalOutList);
		}
		if ((selectedList != null) && (selectedList.size() > 0))
			selectedList.remove(0);  // remove the first one
		AddAPayment(selectedList, allPayments);
		String[] selectedPaymentTypeValues = null;
		if (selectedList.size() > 0)
		{
			selectedPaymentTypeValues = new String[selectedList.size()];
		}
		int i=0;
		for (PaymentTypeData payment : selectedList)
		{
			selectedPaymentTypeValues[i++] = payment.getId().toString();
		}
		if (accountAction == AccountActionTypes.LOAN_REPAYMENT)
		{
			form.setRepayments(selectedPaymentTypeValues);
		}
		else if (accountAction == AccountActionTypes.FEE_REPAYMENT)
		{		
			form.setFees(selectedPaymentTypeValues);
		}
		else if (accountAction == AccountActionTypes.DISBURSAL)
		{		
			form.setDisbursements(selectedPaymentTypeValues);
		}
		else if (accountAction == AccountActionTypes.SAVINGS_DEPOSIT)
		{		
			form.setDeposits(selectedPaymentTypeValues);
		}
		else if (accountAction == AccountActionTypes.SAVINGS_WITHDRAWAL)
		{		
			form.setWithdrawals(selectedPaymentTypeValues);
		}
			
		 
	}
	
	private void setFormFields(List<PaymentTypeData> allPayments, AcceptedPaymentTypeActionForm form) throws Exception
	{
		for (int i=0; i < accountActionTypesForAcceptedPaymentType.length; i++)
			setSelectedPaymentTypesForAnAccountActionType(accountActionTypesForAcceptedPaymentType[i], form);
		setActionForm(form);
	}
	
	private String[] toStringArray(List<PaymentTypeData> list)
	{
		String[] stringArray = null;
		if ((list != null) && (list.size() > 0))
		{
			stringArray = new String[list.size()];
			int i=0;
			for (PaymentTypeData payment : list)
				stringArray[i] = payment.getId().toString();
		}
		return stringArray;
	}
	
	private void setFormFieldsForReverse(AccountActionTypes accountAction, AcceptedPaymentTypeActionForm form)
	{
		if (accountAction == AccountActionTypes.LOAN_REPAYMENT)
		{
			form.setRepayments(toStringArray(repaymentOutList));
		}
		else if (accountAction == AccountActionTypes.FEE_REPAYMENT)
		{		
			form.setFees(toStringArray(feeOutList));
		}
		else if (accountAction == AccountActionTypes.DISBURSAL)
		{		
			form.setDisbursements(toStringArray(disbursementOutList));
		}
		else if (accountAction == AccountActionTypes.SAVINGS_DEPOSIT)
		{		
			form.setDeposits(toStringArray(depositOutList));
		}
		else if (accountAction == AccountActionTypes.SAVINGS_WITHDRAWAL)
		{		
			form.setWithdrawals(toStringArray(withdrawalOutList));
		}
			
	}
	
	public void testUpdate() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		HibernateUtil.getSessionTL();
		createFlowAndAddToRequest(AcceptedPaymentTypeAction.class);
		setRequestPathInfo("/acceptedPaymentTypeAction.do");
		addRequestParameter("method", "update");
		List<PaymentTypeData> allPayments  = getAllPaymentTypes(request.getLocale());
		AcceptedPaymentTypeActionForm acceptedPaymentTypeActionForm = new AcceptedPaymentTypeActionForm();
		AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();
		acceptedPaymentTypeActionForm.setAllPaymentTypes(allPayments);
		boolean reverse = false;
		for (int i=0; i < accountActionTypesForAcceptedPaymentType.length; i++)
			setPaymentTypesForAnAccountAction(allPayments, 
					accountActionTypesForAcceptedPaymentType[i], persistence, request, reverse);
		
		setFormFields(allPayments, acceptedPaymentTypeActionForm);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		HibernateUtil.flushAndCloseSession();
		
		// reverse
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		HibernateUtil.getSessionTL();
		createFlowAndAddToRequest(AcceptedPaymentTypeAction.class);
		setRequestPathInfo("/acceptedPaymentTypeAction.do");
		addRequestParameter("method", "update");
		reverse = true;
		for (int i=0; i < accountActionTypesForAcceptedPaymentType.length; i++)
			setPaymentTypesForAnAccountAction(allPayments, accountActionTypesForAcceptedPaymentType[i],
					persistence, request, reverse);
		for (int i=0; i < accountActionTypesForAcceptedPaymentType.length; i++)
			setFormFieldsForReverse(accountActionTypesForAcceptedPaymentType[i], acceptedPaymentTypeActionForm);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		HibernateUtil.flushAndCloseSession();
	
		
		}
		
	
	
	public void testCancel() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/acceptedPaymentTypeAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancel_success.toString());
	}


}


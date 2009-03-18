package org.mifos.application.acceptedpaymenttype.struts.action;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.mifos.application.acceptedpaymenttype.business.AcceptedPaymentType;
import org.mifos.application.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.application.acceptedpaymenttype.struts.actionform.AcceptedPaymentTypeActionForm;
import org.mifos.application.acceptedpaymenttype.util.helpers.AcceptedPaymentTypeConstants;
import org.mifos.application.acceptedpaymenttype.util.helpers.PaymentTypeData;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AcceptedPaymentTypeActionTest extends MifosMockStrutsTestCase{

	public AcceptedPaymentTypeActionTest() throws SystemException, ApplicationException {
        super();
    }

    private UserContext userContext;
	private final Short DEFAULT_LOCALE = 1;
	private String flowKey;
	String[] EXPECTED_ALL_PAYMENT_TYPES = {"Cash", "Voucher", "Cheque"};
	private List<PaymentTypeData> feeOutList = null;
	private List<PaymentTypeData> disbursementOutList = null;
	private List<PaymentTypeData> repaymentOutList = null;
	private List<PaymentTypeData> depositOutList = null;
	private List<PaymentTypeData> withdrawalOutList = null;
	private String[] feeAcceptedPaymentTypes = null;
	private String[] disbursementAcceptedPaymentTypes = null;
	private String[] repaymentAcceptedPaymentTypes = null;
	private String[] depositAcceptedPaymentTypes = null;
	private String[] withdrawalAcceptedPaymentTypes = null;
	
	
	
	
	@Override
	protected void tearDown() throws Exception {
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
		//AcceptedPaymentTypeActionForm acceptedPaymentTypeActionForm = 
		//	(AcceptedPaymentTypeActionForm) request
		//		.getSession().getAttribute("acceptedpaymenttypeactionform");
		// for default locale all the payment types are Cash, Checque and Voucher
		List<PaymentTypeData> list = getAllPaymentTypes(DEFAULT_LOCALE);
		assertTrue(compareLists(list, EXPECTED_ALL_PAYMENT_TYPES, 3));	
		
		
		// the data on the left list box and the right list box always add up to the all payment types
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
	
	protected List<MasterDataEntity> getMasterEntities(Class clazz,
			Short localeId) throws ServiceException, PersistenceException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		return masterDataService.retrieveMasterEntities(clazz, localeId);
	}
	
	private List<PaymentTypeData> getAllPaymentTypes(Short localeId) throws Exception
	{
	     List<PaymentTypeData> paymentTypeList = new ArrayList();
	     PaymentTypeData payment = null;
	     Short id = 0;
	     List<MasterDataEntity> paymentTypes = getMasterEntities(PaymentTypeEntity.class, localeId);
	     for (MasterDataEntity masterDataEntity : paymentTypes) 
	     {
			PaymentTypeEntity paymentType = (PaymentTypeEntity) masterDataEntity;
			id = paymentType.getId();
	    	payment = new PaymentTypeData(id);
	    	payment.setName(paymentType.getName());
	    	paymentTypeList.add(payment);
		}
	     
	     return paymentTypeList;
	}
	
	/*private String GetPaymentTypeName(Short paymentTypeId, List<PaymentTypeData> paymentTypes)
	{
		
		for (PaymentTypeData paymentTypeData : paymentTypes)
		{
			Short paymentId = paymentTypeData.getId();
			if (paymentId.shortValue() == paymentTypeId.shortValue())
				 return paymentTypeData.getName();
		}
		return "";
	}*/
	
	private void RemoveFromInList(List<PaymentTypeData> list, Short paymentTypeId)
	{
		for (int i=list.size()-1; i >= 0; i--) 
		{
			if (list.get(i).getId().shortValue() == paymentTypeId.shortValue())
				list.remove(i);
		}
	}
	
	private void setPaymentTypesForATransactionType(List<PaymentTypeData> payments, TrxnTypes transactionType,
			AcceptedPaymentTypePersistence paymentTypePersistence, HttpServletRequest request, boolean reverse) throws Exception
	{
		
		Short transactionId = transactionType.getValue();
		// get user defined accepted payment types from database and this will go to the right list box
		List<AcceptedPaymentType> paymentTypeList = paymentTypePersistence.getAcceptedPaymentTypesForATransaction(transactionId);
		// the list box on the left will get all payment types - the accepted payment types (on the right listbox)
		List<PaymentTypeData> inList = new ArrayList(payments);
		// this outList will hold the current accepted payment types
		List<PaymentTypeData> outList = new ArrayList();
	
		PaymentTypeData data = null;
		for (AcceptedPaymentType paymentType : paymentTypeList)
		{
			Short paymentTypeId = paymentType.getPaymentTypeEntity().getId();
			data = new PaymentTypeData(paymentTypeId);
			PaymentTypeEntity paymentTypeEntity = paymentType.getPaymentTypeEntity();
			data.setName(paymentTypeEntity.getName());
			data.setAcceptedPaymentTypeId(paymentType.getAcceptedPaymentTypeId());
			outList.add(data);
			RemoveFromInList(inList, paymentTypeId);
		}
		if (transactionType == TrxnTypes.loan_repayment)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_REPAYMENT_LIST,
					inList, request);
			if (reverse == false)
				feeOutList = outList;
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_REPAYMENT_LIST,
					outList, request);
		}
		else if (transactionType == TrxnTypes.fee)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_FEE_LIST,
					inList, request);
			if (reverse == false)
				repaymentOutList = outList;
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_FEE_LIST,
					outList, request);
		}
		else if (transactionType == TrxnTypes.loan_disbursement)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_DISBURSEMENT_LIST,
					inList, request);
			if (reverse == false)
				disbursementOutList = outList;
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_DISBURSEMENT_LIST,
					outList, request);
		}
		else if (transactionType == TrxnTypes.savings_deposit)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_DEPOSIT_LIST,
					inList, request);
			if (reverse == false)
				depositOutList = outList;
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_DEPOSIT_LIST,
					outList, request);
		}
		else if (transactionType == TrxnTypes.savings_withdrawal)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_WITHDRAWAL_LIST,
					inList, request);
			if (reverse == false)
				withdrawalOutList = outList;
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_WITHDRAWAL_LIST,
					outList, request);
		}
		else
			throw new Exception("Unknow account action for accepted payment type " + transactionType.toString());
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
	
	private void setSelectedPaymentTypesForATransactionType(List<PaymentTypeData> allPayments, TrxnTypes transactionType,
			AcceptedPaymentTypeActionForm form )
		throws Exception
	{
		List<PaymentTypeData> selectedList = null;
		
		if (transactionType == TrxnTypes.loan_repayment)
		{
			selectedList = new ArrayList(repaymentOutList);
			
		}
		else if (transactionType == TrxnTypes.fee)
		{		
			selectedList = new ArrayList(feeOutList);
		}
		else if (transactionType == TrxnTypes.loan_disbursement)
		{		
			selectedList = new ArrayList(disbursementOutList);
		}
		else if (transactionType == TrxnTypes.savings_deposit)
		{		
			selectedList = new ArrayList(depositOutList);
		}
		else if (transactionType == TrxnTypes.savings_withdrawal)
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
		if (transactionType == TrxnTypes.loan_repayment)
		{
			form.setRepayments(selectedPaymentTypeValues);
			repaymentAcceptedPaymentTypes = selectedPaymentTypeValues;
		}
		else if (transactionType == TrxnTypes.fee)
		{		
			form.setFees(selectedPaymentTypeValues);
			feeAcceptedPaymentTypes = selectedPaymentTypeValues;
		}
		else if (transactionType == TrxnTypes.loan_disbursement)
		{		
			form.setDisbursements(selectedPaymentTypeValues);
			disbursementAcceptedPaymentTypes = selectedPaymentTypeValues;
		}
		else if (transactionType == TrxnTypes.savings_deposit)
		{		
			form.setDeposits(selectedPaymentTypeValues);
			depositAcceptedPaymentTypes = selectedPaymentTypeValues;
		}
		else if (transactionType == TrxnTypes.savings_withdrawal)
		{		
			form.setWithdrawals(selectedPaymentTypeValues);
			withdrawalAcceptedPaymentTypes = selectedPaymentTypeValues;
		}
			
		 
	}
	
	private void setFormFields(List<PaymentTypeData> allPayments, AcceptedPaymentTypeActionForm form) throws Exception
	{
		for (int i=0; i < TrxnTypes.values().length; i++)
			setSelectedPaymentTypesForATransactionType(allPayments, TrxnTypes.values()[i], form);
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
	
	private void setFormFieldsForReverse(TrxnTypes transactionType, AcceptedPaymentTypeActionForm form)
	{
		if (transactionType == TrxnTypes.loan_repayment)
		{
			form.setRepayments(toStringArray(repaymentOutList));
		}
		else if (transactionType == TrxnTypes.fee)
		{		
			form.setFees(toStringArray(feeOutList));
		}
		else if (transactionType == TrxnTypes.loan_disbursement)
		{		
			form.setDisbursements(toStringArray(disbursementOutList));
		}
		else if (transactionType == TrxnTypes.savings_deposit)
		{		
			form.setDeposits(toStringArray(depositOutList));
		}
		else if (transactionType == TrxnTypes.savings_withdrawal)
		{		
			form.setWithdrawals(toStringArray(withdrawalOutList));
		}
			
	}
	
	private boolean Find(Short paymentId, String[] payments)
	{
		for (int i=0; i < payments.length; i++)
		{
			Short id = Short.parseShort(payments[i]);
			if (id.shortValue() == paymentId.shortValue())
				return true;
		}
		return false;
	}
	
	private void comparePaymentTypes(List<AcceptedPaymentType> paymentFromDB,
			TrxnTypes transactionType, Locale locale, List<PaymentTypeData> allPayments )
	{
		String[] savedPaymentTypes = null;
		if (transactionType == TrxnTypes.loan_repayment)
		{
			savedPaymentTypes = repaymentAcceptedPaymentTypes;
		}
		else if (transactionType == TrxnTypes.fee)
		{		
			savedPaymentTypes = feeAcceptedPaymentTypes;
		}
		else if (transactionType == TrxnTypes.loan_disbursement)
		{		
			savedPaymentTypes = disbursementAcceptedPaymentTypes;
		}
		else if (transactionType == TrxnTypes.savings_deposit)
		{		
			savedPaymentTypes = depositAcceptedPaymentTypes;
		}
		else if (transactionType == TrxnTypes.savings_withdrawal)
		{		
			savedPaymentTypes = withdrawalAcceptedPaymentTypes;
		}
		assertTrue(savedPaymentTypes.length == paymentFromDB.size());
		for (AcceptedPaymentType paymentType: paymentFromDB)
		{
			Short paymentTypeId = paymentType.getPaymentTypeEntity().getId();
			//String payment = GetPaymentTypeName(paymentTypeId, allPayments);
			assertTrue(Find(paymentTypeId, savedPaymentTypes));
		}
			
			
	}
	
	private void verify(AcceptedPaymentTypePersistence persistence, Locale locale, List<PaymentTypeData> allPayments) throws Exception
	{
		for (int i=0; i < TrxnTypes.values().length; i++)
		{
			Short transactionId = TrxnTypes.values()[i].getValue();
			List<AcceptedPaymentType> paymentTypeList = persistence.getAcceptedPaymentTypesForATransaction(transactionId);
			comparePaymentTypes(paymentTypeList, TrxnTypes.values()[i], locale, allPayments);
		}
	}
	
	public void testUpdate() throws Exception {
		UserContext userContext = TestObjectFactory.getContext();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		StaticHibernateUtil.getSessionTL();
		createFlowAndAddToRequest(AcceptedPaymentTypeAction.class);
		setRequestPathInfo("/acceptedPaymentTypeAction.do");
		addRequestParameter("method", "update");
		Locale locale = request.getLocale();
		List<PaymentTypeData> allPayments  = getAllPaymentTypes(userContext.getLocaleId());
		AcceptedPaymentTypeActionForm acceptedPaymentTypeActionForm = new AcceptedPaymentTypeActionForm();
		AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();

		// reserse = true after this test and we want to reserve data to get back to the 
		// payment types in the database before this test is executed
		boolean reverse = false;
		for (int i=0; i < TrxnTypes.values().length; i++)
			setPaymentTypesForATransactionType(allPayments, 
					TrxnTypes.values()[i], persistence, request, reverse);
		// set data on the form to represent user's selected payment types
		setFormFields(allPayments, acceptedPaymentTypeActionForm);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		// verify that the data from database match with the ones we keep
		verify(persistence, locale, allPayments);
		StaticHibernateUtil.flushAndCloseSession();
		
		// reverse data to get back to data before test
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		StaticHibernateUtil.getSessionTL();
		createFlowAndAddToRequest(AcceptedPaymentTypeAction.class);
		setRequestPathInfo("/acceptedPaymentTypeAction.do");
		addRequestParameter("method", "update");
		reverse = true;
		for (int i=0; i < TrxnTypes.values().length; i++)
			setPaymentTypesForATransactionType(allPayments, TrxnTypes.values()[i],
					persistence, request, reverse);
		for (int i=0; i < TrxnTypes.values().length; i++)
			setFormFieldsForReverse(TrxnTypes.values()[i], acceptedPaymentTypeActionForm);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		// verify data from database and what we have in memory
		verify(persistence, locale, allPayments);
		StaticHibernateUtil.flushAndCloseSession();
	
		
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


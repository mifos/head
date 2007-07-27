package org.mifos.application.acceptedpaymenttype.struts.action;



import org.mifos.application.acceptedpaymenttype.struts.actionform.AcceptedPaymentTypeActionForm;
import org.mifos.application.acceptedpaymenttype.struts.action.AcceptedPaymentTypeAction;
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
import java.util.List;
//import org.mifos.application.accounts.util.helpers.AccountActionTypes;

public class AcceptedPaymentTypeActionTest extends MifosMockStrutsTestCase{

	private UserContext userContext;
	private String flowKey;
	//private final Short DEFAULT_LOCALE = 1;
	
	
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
			//assertEquals(second[index], first.get(index).getName());
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
		String[] EXPECTED_ALL_PAYMENT_TYPES = {"Cash", "Voucher", "Check"};
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


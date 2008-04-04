package org.mifos.application.accounts.loan.struts.actionforms;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Date;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.junit.Test;
import org.mifos.application.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.application.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.MoneyFactory;

public class LoanAccountActionFormTest extends MifosTestCase {

	private LoanAccountActionForm form;
	private PaymentDataTemplate paymentMock;

	@Test
	public void testShouldAddErrorIfTransactionDateForAPaymentIsInFuture() {
		expect(paymentMock.getTransactionDate()).andReturn(
				DateUtils.getDateFromToday(1));
		replay(paymentMock);
		ActionErrors errors = new ActionErrors();
		form.validateTransactionDate(errors, paymentMock, DateUtils
				.getDateFromToday(-3));
		verify(paymentMock);
		assertErrorKey(errors,
				LoanExceptionConstants.INVALIDTRANSACTIONDATEFORPAYMENT);
	}

	@Test
	public void testShouldNotAddErrorIfTransactionDateForAPaymentIsToday() {
		validateForNoErrorsOnDate(DateUtils.currentDate());
	}

	@Test
	public void testShouldNotAddErrorIfTransactionDateForAPaymentIsInPast() {
		validateForNoErrorsOnDate(DateUtils.getDateFromToday(-1));
	}

	public void testShouldAddErrorWhenTransactionDateOnDisbursementDate()
			throws Exception {
		validateWhenTransactionDateInvalidForDisbursementDate(DateUtils
				.currentDate());
	}

	public void testShouldAddErrorIfTransactionDateBeforeDisbursementDate()
			throws Exception {
		validateWhenTransactionDateInvalidForDisbursementDate(DateUtils
				.getDateFromToday(1));
	}

	public void testShouldNotAddErrorIfTransactionDateAfterDisbursementDate()
			throws Exception {
		expect(paymentMock.getTransactionDate()).andReturn(
				DateUtils.currentDate()).anyTimes();
		replay(paymentMock);
		ActionErrors errors = new ActionErrors();
		form.validateTransactionDate(errors, paymentMock, DateUtils
						.getDateFromToday(-1));
		verify(paymentMock);
		assertTrue(errors.isEmpty());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		form = new LoanAccountActionForm();
		paymentMock = createMock(PaymentDataTemplate.class);
		expect(paymentMock.getTotalAmount()).andReturn(MoneyFactory.ZERO);
	}

	private void validateForNoErrorsOnDate(Date transactionDate) {
		expect(paymentMock.getTransactionDate()).andReturn(transactionDate)
				.anyTimes();
		replay(paymentMock);
		ActionErrors errors = new ActionErrors();
		form.validateTransactionDate(errors, paymentMock, DateUtils
				.getDateFromToday(-3));
		verify(paymentMock);
		assertTrue(errors.isEmpty());
	}

	private void assertErrorKey(ActionErrors errors, String expectedErrorKey) {
		assertEquals(1, errors.size());
		assertEquals(expectedErrorKey, ((ActionMessage) errors.get().next())
				.getKey());
	}

	private void validateWhenTransactionDateInvalidForDisbursementDate(
			Date disbursementDate) {
		expect(paymentMock.getTransactionDate()).andReturn(
				DateUtils.currentDate()).anyTimes();
		replay(paymentMock);
		ActionErrors errors = new ActionErrors();
		form.validateTransactionDate(errors, paymentMock, disbursementDate);
		verify(paymentMock);
		assertErrorKey(errors, LoanExceptionConstants.INVALIDTRANSACTIONDATE);
	}
}

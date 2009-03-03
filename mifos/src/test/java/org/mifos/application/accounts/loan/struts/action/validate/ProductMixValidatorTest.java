package org.mifos.application.accounts.loan.struts.action.validate;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.Arrays;
import java.util.Collections;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productsmix.business.service.ProductMixBusinessService;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;

public class ProductMixValidatorTest extends MifosIntegrationTest {

	public ProductMixValidatorTest() throws SystemException, ApplicationException {
        super();
    }

    private ConfigurationBusinessService configServiceMock;
	private ProductMixBusinessService productMixBusinessServiceMock;
	private LoanBO loanMock;
	private CustomerBO customerMock;

	@Override
	protected void setUp() throws Exception {
		configServiceMock = createMock(ConfigurationBusinessService.class);
		productMixBusinessServiceMock = createMock(ProductMixBusinessService.class);
		loanMock = createMock(LoanBO.class);
		customerMock = createMock(CustomerBO.class);
	}

	public void testShouldDetectProductMixConflicts() throws Exception {
		short PRD_OFFERING_ID_TWO = (short) 2;

		LoanBO loanMock1 = createMock(LoanBO.class);
		LoanBO loanMock2 = createMock(LoanBO.class);
		LoanOfferingBO loanOfferingMock1 = createMock(LoanOfferingBO.class);
		LoanOfferingBO loanOfferingMock2 = createMock(LoanOfferingBO.class);
		expect(loanMock2.getLoanOffering()).andReturn(loanOfferingMock2);
		expect(loanMock1.getLoanOffering()).andReturn(loanOfferingMock1);
		expect(loanOfferingMock2.getPrdOfferingId()).andReturn(
				PRD_OFFERING_ID_TWO);

		expect(
				productMixBusinessServiceMock.canProductsCoExist(
						loanOfferingMock2, loanOfferingMock1)).andReturn(false);
		
		replay(loanMock1, loanMock2, loanOfferingMock1, loanOfferingMock2, productMixBusinessServiceMock);
		try {
			new ProductMixValidator(configServiceMock, productMixBusinessServiceMock) {
				@Override
				void handleConflict(LoanBO newloan, LoanBO loan)
						throws AccountException {
					throw new AccountException("Some exception code");
				}
			}.validateProductMix(loanMock2, Arrays.asList(loanMock1));

			fail("Product mix conflict not detected");
			verify(loanMock1, loanMock2, loanOfferingMock1, loanOfferingMock2, productMixBusinessServiceMock);
		}
		catch (AccountException e) {

		}
	}

	public void testShouldAllowValidProductMix() throws Exception {
		LoanBO loanMock1 = createMock(LoanBO.class);
		LoanBO loanMock2 = createMock(LoanBO.class);
		LoanOfferingBO loanOfferingMock1 = createMock(LoanOfferingBO.class);
		LoanOfferingBO loanOfferingMock2 = createMock(LoanOfferingBO.class);
		expect(loanMock1.getLoanOffering()).andReturn(loanOfferingMock1);
		expect(loanMock2.getLoanOffering()).andReturn(loanOfferingMock2);
		expect(
				productMixBusinessServiceMock.canProductsCoExist(
						 loanOfferingMock2, loanOfferingMock1)).andReturn(true);
		replay(loanMock1, loanMock2, loanOfferingMock1, loanOfferingMock2, productMixBusinessServiceMock);
		try {
			new ProductMixValidator(configServiceMock, productMixBusinessServiceMock) {
				@Override
				void handleConflict(LoanBO newloan, LoanBO loan)
						throws AccountException {
					throw new AccountException("Some exception code");
				}
			}.validateProductMix(loanMock2, Arrays.asList(loanMock1));
			verify(loanMock1, loanMock2, loanOfferingMock1, loanOfferingMock2, productMixBusinessServiceMock);
		}
		catch (AccountException e) {
			fail("Invalid Product mix conflict detected");
		}
	}

	public void testShouldGetLoansIfCustomerIsCoSigningClientInGlimMode()
			throws Exception {
		expect(configServiceMock.isGlimEnabled()).andReturn(true);
		replay(loanMock, customerMock, configServiceMock);
		assertEquals(Arrays.asList(loanMock),
				getProductMixValidatorWithCosigningClientTrue()
						.getLoansToCheckAgainstProductMix(customerMock,
								Arrays.asList(loanMock)));
		verify(loanMock, customerMock, configServiceMock);
	}

	public void testShouldReturnEmptyListIfCustomerIsNotACoSigningClientInGlimMode()
			throws Exception {
		expect(configServiceMock.isGlimEnabled()).andReturn(true);
		replay(loanMock, customerMock, configServiceMock);
		assertEquals(Collections.EMPTY_LIST,
				getProductMixValidatorWithCosigningClientFalse()
						.getLoansToCheckAgainstProductMix(customerMock,
								Arrays.asList(loanMock)));
		verify(loanMock, customerMock, configServiceMock);
	}


	public void testShouldReturnAllLoansIfNotGlim() throws Exception {
		expect(configServiceMock.isGlimEnabled()).andReturn(false);
		replay(loanMock, customerMock, configServiceMock);
		assertEquals(Arrays.asList(loanMock), new ProductMixValidator(
				configServiceMock, new ProductMixBusinessService())
				.getLoansToCheckAgainstProductMix(customerMock, Arrays
						.asList(loanMock)));
		verify(loanMock, customerMock, configServiceMock);
	}

	private ProductMixValidator getProductMixValidatorWithCosigningClientTrue() {
		return new ProductMixValidator(configServiceMock,
				new ProductMixBusinessService()) {
			@Override
			boolean isCustomerACoSigningClient(CustomerBO customer, LoanBO loan)
					throws ServiceException {
				return true;
			}
		};
	}

	private ProductMixValidator getProductMixValidatorWithCosigningClientFalse() {
		return new ProductMixValidator(configServiceMock,
				new ProductMixBusinessService()) {
			@Override
			boolean isCustomerACoSigningClient(CustomerBO customer, LoanBO loan)
					throws ServiceException {
				return false;
			}
		};
	}
}

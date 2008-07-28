package org.mifos.application.customer.group.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.easymock.classextension.EasyMock.expectLastCall;

import java.util.ArrayList;
import java.util.Arrays;

import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.MoneyFactory;


public class GroupPerformanceHistoryEntityTest extends MifosTestCase{
	
	private LoanBO loan;
	private ConfigurationBusinessService configServiceMock;
	private AccountBusinessService accountBusinessServiceMock;
	private LoanOfferingBO loanOffering;
	private CustomerBO customerMock;
	private ClientPerformanceHistoryEntity clientPerfHistoryMock;


	public void testUpdateOnDisbursementGetsCoSigningClientsForGlim() throws Exception {
		expect(configServiceMock.isGlimEnabled()).andReturn(true);
		clientPerfHistoryMock.updateOnDisbursement(loanOffering);

		expect(customerMock.getPerformanceHistory()).andReturn(clientPerfHistoryMock);
		expectLastCall().atLeastOnce();
		
		expect(accountBusinessServiceMock.getCoSigningClientsForGlim(loan.getAccountId())).andReturn(Arrays.asList(customerMock));
		replay(configServiceMock, accountBusinessServiceMock, customerMock, clientPerfHistoryMock);
		
		new GroupPerformanceHistoryEntity(configServiceMock, accountBusinessServiceMock).updateOnDisbursement(loan, MoneyFactory.ZERO);
		verify(configServiceMock, accountBusinessServiceMock, customerMock, clientPerfHistoryMock);
	}

	public void testUpdateOnDisbursementDoesNotGetCoSigningClientsIfNotGlim() throws Exception {
		expect(configServiceMock.isGlimEnabled()).andReturn(false);
		replay(configServiceMock, accountBusinessServiceMock);
		new GroupPerformanceHistoryEntity(configServiceMock, accountBusinessServiceMock).updateOnDisbursement(loan, MoneyFactory.ZERO);
		verify(configServiceMock, accountBusinessServiceMock);
	}	

	public void testUpdateOnWriteOffDoesNotGetCoSigningClientsIfNotGlim() throws Exception {
		expect(configServiceMock.isGlimEnabled()).andReturn(false);
		replay(configServiceMock, accountBusinessServiceMock);
		new GroupPerformanceHistoryEntity(configServiceMock, accountBusinessServiceMock).updateOnWriteOff(loan);
		verify(configServiceMock, accountBusinessServiceMock);
	}	
	
	public void testUpdateOnWriteOffGetsCoSigningClientsForGlim() throws Exception {
		expect(configServiceMock.isGlimEnabled()).andReturn(true);
		expect(customerMock.getPerformanceHistory()).andReturn(clientPerfHistoryMock);
		expectLastCall().atLeastOnce();
		clientPerfHistoryMock.updateOnWriteOff(loanOffering);
		expect(accountBusinessServiceMock.getCoSigningClientsForGlim(loan.getAccountId())).andReturn(Arrays.asList(customerMock));
		replay(configServiceMock, accountBusinessServiceMock,customerMock,clientPerfHistoryMock);
		new GroupPerformanceHistoryEntity(configServiceMock, accountBusinessServiceMock).updateOnWriteOff(loan);
		verify(configServiceMock, accountBusinessServiceMock,customerMock,clientPerfHistoryMock);
	}
	
	@Override
	protected void setUp() throws Exception {
		loanOffering = LoanOfferingBO.createInstanceForTest((short)1);
		loan = LoanBO.createInstanceForTest(loanOffering);
		configServiceMock = createMock(ConfigurationBusinessService.class);
		accountBusinessServiceMock = createMock(AccountBusinessService.class);
		clientPerfHistoryMock = createMock(ClientPerformanceHistoryEntity.class);		
		customerMock = createMock(CustomerBO.class);
	}	
}

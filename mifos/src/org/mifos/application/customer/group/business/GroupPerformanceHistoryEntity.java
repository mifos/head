package org.mifos.application.customer.group.business;
import static org.mifos.application.customer.group.business.GroupLoanCounter.TRANSFORM_GROUP_LOAN_COUNTER_TO_LOAN_CYCLE;
import static org.mifos.framework.util.CollectionUtils.find;
import static org.mifos.framework.util.CollectionUtils.select;
import static org.mifos.framework.util.helpers.NumberUtils.SHORT_ZERO;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPerformanceHistory;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryUpdater.UpdateClientPerfHistoryForGroupLoanOnDisbursement;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryUpdater.UpdateClientPerfHistoryForGroupLoanOnRepayment;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryUpdater.UpdateClientPerfHistoryForGroupLoanOnReversal;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryUpdater.UpdateClientPerfHistoryForGroupLoanOnWriteOff;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.Predicate;


public class GroupPerformanceHistoryEntity extends CustomerPerformanceHistory {

	private Integer id;

	private Integer clientCount;

	private Money lastGroupLoanAmount;

	private Money avgLoanForMember;

	private Money totalOutstandingPortfolio;

	private Money totalSavings;

	private Money portfolioAtRisk;

	private GroupBO group;
	
	
	
	/**
	 * stores the loan cycle information based
	 * on products 
	 */
	private Set<GroupLoanCounter> loanCounters;

	private ConfigurationBusinessService configService;

	private AccountBusinessService accountBusinessService;
	
	public GroupPerformanceHistoryEntity(GroupBO group) {
		this(0, new Money(), new Money(), new Money(), new Money(), new Money());
		this.group = group;
	}

	private GroupPerformanceHistoryEntity(Integer clientCount,
			Money lastGroupLoanAmount, Money avgLoanForMember,
			Money totalOutstandingPortfolio, Money totalSavings,
			Money portfolioAtRisk, ConfigurationBusinessService configService,
			AccountBusinessService accountBusinessService) {
		this.portfolioAtRisk = portfolioAtRisk;
		this.totalOutstandingPortfolio = totalOutstandingPortfolio;
		this.totalSavings = totalSavings;
		this.avgLoanForMember = avgLoanForMember;
		this.lastGroupLoanAmount = lastGroupLoanAmount;
		this.clientCount = clientCount;
		this.configService = configService;
		this.accountBusinessService = accountBusinessService;
		this.loanCounters = new HashSet<GroupLoanCounter>();
		this.id = null;
	}

	GroupPerformanceHistoryEntity(ConfigurationBusinessService configService,
			AccountBusinessService accountBusinessService) {
		this(0, new Money(), new Money(), new Money(), new Money(),
				new Money(), configService, accountBusinessService);
	}

	public GroupPerformanceHistoryEntity(Integer clientCount,
			Money lastGroupLoanAmount, Money avgLoanForMember,
			Money totalOutstandingPortfolio, Money totalSavings,
			Money portfolioAtRisk) {
		this(clientCount, lastGroupLoanAmount, avgLoanForMember,
				totalOutstandingPortfolio, totalSavings, portfolioAtRisk,
				new ConfigurationBusinessService(),
				new AccountBusinessService());
	}

	protected GroupPerformanceHistoryEntity() {
		this(0, null, null, null, null, null);
	}

	public Integer getId() {
		return id;
	}

	void setId(Integer id) {
		this.id = id;
	}

	private Money getAvgLoanForMember() {
		return avgLoanForMember;
	}

	void setAvgLoanForMember(Money avgLoanForMember) {
		this.avgLoanForMember = avgLoanForMember;
	}

	public Integer getClientCount() {
		return clientCount;
	}

	void setClientCount(Integer clientCount) {
		this.clientCount = clientCount;
	}

	public Money getLastGroupLoanAmount() {
		return lastGroupLoanAmount;
	}

	public void setLastGroupLoanAmount(Money lastGroupLoanAmount) {
		this.lastGroupLoanAmount = lastGroupLoanAmount;
	}

	private Money getTotalOutstandingPortfolio() {
		return totalOutstandingPortfolio;
	}

	void setTotalOutstandingPortfolio(Money totalOutstandingPortfolio) {
		this.totalOutstandingPortfolio = totalOutstandingPortfolio;
	}

	public Money getPortfolioAtRisk() {
		return portfolioAtRisk;
	}

	void setPortfolioAtRisk(Money portfolioAtRisk) {
		this.portfolioAtRisk = portfolioAtRisk;
	}

	private Money getTotalSavings() {
		return totalSavings;
	}

	void setTotalSavings(Money totalSavings) {
		this.totalSavings = totalSavings;
	}

	public GroupBO getGroup() {
		return group;
	}

	void setGroup(GroupBO group) {
		this.group = group;
	}

	public Money getAvgLoanAmountForMember() throws CustomerException {
		Money amountForActiveAccount = new Money();
		Integer countOfActiveLoans = 0;
		List<CustomerBO> clients = getChildren();
		if (clients != null) {
			for (CustomerBO client : clients) {
				amountForActiveAccount = amountForActiveAccount.add(client
						.getOutstandingLoanAmount());
				countOfActiveLoans += client.getActiveLoanCounts();
			}
		}
		if (countOfActiveLoans.intValue() > 0)
			return new Money(String.valueOf(amountForActiveAccount
					.getAmountDoubleValue()
					/ countOfActiveLoans.intValue()));
		return new Money();
	}

	public Integer getActiveClientCount() throws CustomerException {
		List<CustomerBO> clients = getChildren();
		if (clients != null) {
			return Integer.valueOf(clients.size());
		}
		return Integer.valueOf(0);
	}

	public Money getTotalOutStandingLoanAmount() throws CustomerException {
		Money amount = group.getOutstandingLoanAmount();
		//System.out.println("group outstanding amount: " + amount.toString());
		Money clientAmount = new Money();
		List<CustomerBO> clients = getChildren();
		if (clients != null) {
			for (CustomerBO client : clients) {
				amount = amount.add(client.getOutstandingLoanAmount());
				clientAmount = clientAmount.add(client.getOutstandingLoanAmount());
			}
		}
		//System.out.println("client outstanding amount: " + clientAmount.toString());
		
		return amount;
	}

	public Money getTotalSavingsAmount() throws CustomerException {
		Money amount = group.getSavingsBalance();
		List<CustomerBO> clients = getChildren();
		if (clients != null) {
			for (CustomerBO client : clients) {
				amount = amount.add(client.getSavingsBalance());
			}
		}
		return amount;
	}
	
	// this method calculates the PAR using the method LoanBO.hasPortfolioAtRisk() to determine if a loan is in arrears
	public void generatePortfolioAtRisk() throws CustomerException {
		Money amount = group.getBalanceForAccountsAtRisk();
		List<CustomerBO> clients = getChildren();
		if (clients != null) {
			for (CustomerBO client : clients) {
				amount = amount.add(client.getBalanceForAccountsAtRisk());
			}
		}
		double totalOutstandingLoanAmount = getTotalOutStandingLoanAmount().getAmountDoubleValue();
		if (totalOutstandingLoanAmount != 0.0)
			setPortfolioAtRisk(new Money(String.valueOf(amount.getAmountDoubleValue()/totalOutstandingLoanAmount)));
	}
	
	
	
	private List<CustomerBO> getChildren() throws CustomerException {
		return group.getChildren(CustomerLevel.CLIENT,ChildrenStateType.ACTIVE_AND_ONHOLD);
	}

	public void updateLoanCounter(final LoanOfferingBO loanOffering,
			YesNoFlag yesNoFlag) {
		GroupLoanCounter loanCounter = null;
		try {
			loanCounter = findLoanCounterForProduct(loanOffering);
		}
		catch (Exception e) {
		}
		if (loanCounter == null) {
			loanCounter = new GroupLoanCounter(this, loanOffering, yesNoFlag);
			loanCounters.add(loanCounter);
		}
		else {
			loanCounter.updateLoanCounter(yesNoFlag);
		}
	}

	GroupLoanCounter findLoanCounterForProduct(final LoanOfferingBO loanOffering) throws Exception {
		return find(loanCounters, new Predicate<GroupLoanCounter>() {
			public boolean evaluate(GroupLoanCounter loanCounter)
					throws Exception {
				return loanOffering.isOfSameOffering(loanCounter.getLoanOffering());
			}
		});
	}

	public Short getMaxLoanCycleForProduct(final PrdOfferingBO prdOffering) {
		{
			Set<GroupLoanCounter> loanCounters = getLoanCounters();
			try {
				Collection<Short> loanCyclesForProduct = select(
						loanCounters, new Predicate<GroupLoanCounter>() {
							public boolean evaluate(GroupLoanCounter counter) throws Exception {
								return counter.isOfSameProduct(prdOffering);
							}
						}, TRANSFORM_GROUP_LOAN_COUNTER_TO_LOAN_CYCLE);
				return loanCyclesForProduct.isEmpty() ? SHORT_ZERO
						: Collections.max(loanCyclesForProduct);
			}
			catch (Exception e) {
				return SHORT_ZERO;
			}
		}
	}

	public void updateOnDisbursement(LoanBO loan, Money disburseAmount)
			throws AccountException {
		setLastGroupLoanAmount(disburseAmount);
		LoanOfferingBO loanOffering = loan.getLoanOffering();
		updateLoanCounter(loanOffering, YesNoFlag.YES);
		try {
			if (configService.isGlimEnabled()) {
				CollectionUtils.forAllDo(accountBusinessService
						.getCoSigningClientsForGlim(loan.getAccountId()),
						new UpdateClientPerfHistoryForGroupLoanOnDisbursement(loan));
			}
		}
		catch (ServiceException e) {
			throw new AccountException(e);
		}
	}

	public Set<GroupLoanCounter> getLoanCounters() {
		return loanCounters;
	}

	public void updateOnWriteOff(LoanBO loan) throws AccountException {
		updateLoanCounter(loan.getLoanOffering(), YesNoFlag.NO);
		try {
			if (configService.isGlimEnabled()) {
				CollectionUtils.forAllDo(accountBusinessService
						.getCoSigningClientsForGlim(loan.getAccountId()),
						new UpdateClientPerfHistoryForGroupLoanOnWriteOff(loan));
			}
		}
		catch (ServiceException e) {
			throw new AccountException(e);
		}
	}
	
	public void updateOnReversal(LoanBO loan, Money lastLoanAmount)
			throws AccountException {
		setLastGroupLoanAmount(lastLoanAmount);
		updateLoanCounter(loan.getLoanOffering(), YesNoFlag.NO);
		try {
			if (configService.isGlimEnabled()) {
				CollectionUtils.forAllDo(accountBusinessService
						.getCoSigningClientsForGlim(loan.getAccountId()),
						new UpdateClientPerfHistoryForGroupLoanOnReversal(loan));
			}
		}
		catch (ServiceException e) {
			throw new AccountException(e);
		}
	}

	public void updateOnRepayment(LoanBO loan, Money totalAmount) throws AccountException {
		try {
			if (configService.isGlimEnabled()) {
				CollectionUtils.forAllDo(accountBusinessService
						.getCoSigningClientsForGlim(loan.getAccountId()),
						new UpdateClientPerfHistoryForGroupLoanOnRepayment(loan));
			}
		}
		catch (ServiceException e) {
			throw new AccountException(e);
		}
	}
	
}

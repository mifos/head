package org.mifos.application.customer.group.business;

import org.mifos.application.customer.business.CustomerPerformanceHistory;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class GroupPerformanceHistoryEntity extends CustomerPerformanceHistory {

	private Integer id;

	private Integer clientCount;

	private Money lastGroupLoanAmount;

	private Money avgLoanForMember;

	private Money totalOutstandingPortfolio;

	private Money totalSavings;

	private Money portfolioAtRisk;

	private GroupBO group;
	
	public GroupPerformanceHistoryEntity(){}
	
	public GroupPerformanceHistoryEntity(GroupBO group, Integer clientCount,
			Money lastGroupLoanAmount, Money avgLoanForMember,
			Money totalOutstandingPortfolio, Money totalSavings,
			Money portfolioAtRisk) {
		this.group=group;
		this.portfolioAtRisk=portfolioAtRisk;
		this.totalOutstandingPortfolio=totalOutstandingPortfolio;
		this.totalSavings=totalSavings;
		this.avgLoanForMember=avgLoanForMember;
		this.lastGroupLoanAmount=lastGroupLoanAmount;
		this.clientCount=clientCount;
	}

	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Money getAvgLoanForMember() {
		return avgLoanForMember;
	}

	public void setAvgLoanForMember(Money avgLoanForMember) {
		this.avgLoanForMember = avgLoanForMember;
	}

	public Integer getClientCount() {
		return clientCount;
	}

	public void setClientCount(Integer clientCount) {
		this.clientCount = clientCount;
	}

	public Money getLastGroupLoanAmount() {
		return lastGroupLoanAmount;
	}

	public void setLastGroupLoanAmount(Money lastGroupLoanAmount) {
		this.lastGroupLoanAmount = lastGroupLoanAmount;
	}

	public Money getTotalOutstandingPortfolio() {
		return totalOutstandingPortfolio;
	}

	public void setTotalOutstandingPortfolio(Money totalOutstandingPortfolio) {
		this.totalOutstandingPortfolio = totalOutstandingPortfolio;
	}

	public Money getPortfolioAtRisk() {
		return portfolioAtRisk;
	}

	public void setPortfolioAtRisk(Money portfolioAtRisk) {
		this.portfolioAtRisk = portfolioAtRisk;
	}

	public Money getTotalSavings() {
		return totalSavings;
	}

	public void setTotalSavings(Money totalSavings) {
		this.totalSavings = totalSavings;
	}

	public GroupBO getGroup() {
		return group;
	}

	public void setGroup(GroupBO group) {
		this.group = group;
	}
}

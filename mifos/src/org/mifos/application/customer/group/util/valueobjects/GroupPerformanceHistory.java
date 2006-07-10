package org.mifos.application.customer.group.util.valueobjects;

import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

public class GroupPerformanceHistory extends ValueObject {

	private Integer customerId;

	private Integer clientCount;

	private Money lastGroupLoanAmount;

	private Money avgLoanForMember;

	private Money totalOutstandingPortfolio;

	private Money totalSavings;

	private Money portfolioAtRisk;

	private Group group;

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
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

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
}

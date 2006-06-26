/**
 *
 */
package org.mifos.application.customer.util.helpers;

import org.mifos.framework.business.View;

/**
 * @author sumeethaec
 * 
 */
public class PerformanceHistoryView extends View {
	private int numberOfGroups;

	private int numberOfClients;

	private double totalOutstandingPortfolio;

	private double totalSavings;

	public int getNumberOfClients() {
		return numberOfClients;
	}

	public void setNumberOfClients(int numberOfClients) {
		this.numberOfClients = numberOfClients;
	}

	public int getNumberOfGroups() {
		return numberOfGroups;
	}

	public void setNumberOfGroups(int numberOfGroups) {
		this.numberOfGroups = numberOfGroups;
	}

	public double getTotalOutstandingPortfolio() {
		return totalOutstandingPortfolio;
	}

	public void setTotalOutstandingPortfolio(double totalOutstandingPortfolio) {
		this.totalOutstandingPortfolio = totalOutstandingPortfolio;
	}

	public double getTotalSavings() {
		return totalSavings;
	}

	public void setTotalSavings(double totalSavings) {
		this.totalSavings = totalSavings;
	}
}

/**
 * 
 */
package org.mifos.application.customer.center.util.valueobjects;

/**
 * @author sumeethaec
 *
 */
public class PerformanceHistory {
	private int numberOfGroups;
	private int numberOfClients;
	private double totalOutstandingPortfolio;
	private double totalSavings;
	
	/**
	 * Method which returns the numberOfClients	
	 * @return Returns the numberOfClients.
	 */
	public int getNumberOfClients() {
		return numberOfClients;
	}
	/**
	 * Method which sets the numberOfClients
	 * @param numberOfClients The numberOfClients to set.
	 */
	public void setNumberOfClients(int numberOfClients) {
		this.numberOfClients = numberOfClients;
	}
	/**
	 * Method which returns the numberOfGroups	
	 * @return Returns the numberOfGroups.
	 */
	public int getNumberOfGroups() {
		return numberOfGroups;
	}
	/**
	 * Method which sets the numberOfGroups
	 * @param numberOfGroups The numberOfGroups to set.
	 */
	public void setNumberOfGroups(int numberOfGroups) {
		this.numberOfGroups = numberOfGroups;
	}
	/**
	 * Method which returns the totalOutstandingPortfolio	
	 * @return Returns the totalOutstandingPortfolio.
	 */
	public double getTotalOutstandingPortfolio() {
		return totalOutstandingPortfolio;
	}
	/**
	 * Method which sets the totalOutstandingPortfolio
	 * @param totalOutstandingPortfolio The totalOutstandingPortfolio to set.
	 */
	public void setTotalOutstandingPortfolio(double totalOutstandingPortfolio) {
		this.totalOutstandingPortfolio = totalOutstandingPortfolio;
	}
	/**
	 * Method which returns the totalSavings	
	 * @return Returns the totalSavings.
	 */
	public double getTotalSavings() {
		return totalSavings;
	}
	/**
	 * Method which sets the totalSavings
	 * @param totalSavings The totalSavings to set.
	 */
	public void setTotalSavings(double totalSavings) {
		this.totalSavings = totalSavings;
	}
}

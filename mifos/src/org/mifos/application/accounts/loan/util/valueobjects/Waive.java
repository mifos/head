/**
 * 
 */
package org.mifos.application.accounts.loan.util.valueobjects;

/**
 * @author krishankg
 *
 */
public class Waive {
	
	private Integer accountId;
	private Short nextInstallmentId;
	private Short[] dueInstallmentIds;
	private String type;
	private String installmentType;
	private Short personnelId;
	/**
	 * @return Returns the accountId.
	 */
	public Integer getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId The accountId to set.
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	/**
	 * @return Returns the dueInstallmentIds.
	 */
	public Short[] getDueInstallmentIds() {
		return dueInstallmentIds;
	}
	/**
	 * @param dueInstallmentIds The dueInstallmentIds to set.
	 */
	public void setDueInstallmentIds(Short[] dueInstallmentIds) {
		this.dueInstallmentIds = dueInstallmentIds;
	}
	/**
	 * @return Returns the installmentType.
	 */
	public String getInstallmentType() {
		return installmentType;
	}
	/**
	 * @param installmentType The installmentType to set.
	 */
	public void setInstallmentType(String installmentType) {
		this.installmentType = installmentType;
	}
	/**
	 * @return Returns the nextInstallmentId.
	 */
	public Short getNextInstallmentId() {
		return nextInstallmentId;
	}
	/**
	 * @param nextInstallmentId The nextInstallmentId to set.
	 */
	public void setNextInstallmentId(Short nextInstallmentId) {
		this.nextInstallmentId = nextInstallmentId;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return Returns the personnelId.
	 */
	public Short getPersonnelId() {
		return personnelId;
	}
	/**
	 * @param personnelId The personnelId to set.
	 */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}
	

}

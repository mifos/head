
package org.mifos.application.accounts.util.valueobjects;

import java.util.Date;
import java.util.Set;
import java.util.Iterator;

import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

public class AccountActionDate extends ValueObject
{
	private Integer actionDateId;
	private Short installmentId;
	private Account account;
	private Integer customerId;
	private Short currencyId;
	private Date actionDate;
	private Money deposit;
	private Money principal;
	private Money interest;
	private Money penalty;
	private Short parentFlag;
	private Money depositPaid;
	private Money principalPaid;
	private Money interestPaid;
	private Money penaltyPaid;
	private Short paymentStatus = Short.valueOf("0");
	private Set<AccountFeesActionDetail> accountFeesActionDetail = null;
	private Integer versionNo;
	private Date paymentDate;
	private Money miscPenalty;
	private Money miscPenaltyPaid;
	private Money miscFee;
	private Money miscFeePaid;

	/**
	 * @return Returns the miscFee.
	 */
	public Money getMiscFee() {
		return miscFee;
	}

	/**
	 * @param miscFee The miscFee to set.
	 */
	public void setMiscFee(Money miscFee) {
		this.miscFee = miscFee;
	}

	/**
	 * @return Returns the miscFeePaid.
	 */
	public Money getMiscFeePaid() {
		return miscFeePaid;
	}

	/**
	 * @param miscFeePaid The miscFeePaid to set.
	 */
	public void setMiscFeePaid(Money miscFeePaid) {
		this.miscFeePaid = miscFeePaid;
	}

	public void setActionDateId(Integer actionDateId)
	{
		this.actionDateId=actionDateId;
	}

	public Integer getActionDateId()
	{
		return actionDateId;
	}

	public void setInstallmentId(Short installmentId)
	{
		this.installmentId=installmentId;
	}
/*
	public void setAccountId(Integer accountId)
	{
		this.accountId=accountId;
	}
	*/
	public void setAccount(Account account)
	{
		this.account=account;
	}

	public void setCustomerId(Integer customerId)
	{
		this.customerId=customerId;
	}

	public void setCurrencyId(Short currencyId)
	{
		this.currencyId=currencyId;
	}

	public void setActionDate(Date actionDate)
	{
		this.actionDate=actionDate;
	}

	public void setDeposit(Money deposit)
	{
		this.deposit=deposit;
	}

	public void setPrincipal(Money principal)
	{
		this.principal=principal;
	}

	public void setInterest(Money interest)
	{
		this.interest=interest;
	}

	public void setPenalty(Money penalty)
	{
		this.penalty=penalty;
	}

	public void setDepositPaid(Money depositPaid)
	{
		this.depositPaid=depositPaid;
	}

	public void setPrincipalPaid(Money principalPaid)
	{
		this.principalPaid=principalPaid;
	}

	public void setInterestPaid(Money interestPaid)
	{
		this.interestPaid=interestPaid;
	}

	public void setPenaltyPaid(Money penaltyPaid)
	{
		this.penaltyPaid=penaltyPaid;
	}

	public void setParentFlag(Short parentFlag)
	{
		this.parentFlag=parentFlag;
	}

	public void setPaymentStatus(Short paymentStatus)
	{
		this.paymentStatus=paymentStatus;
	}
	public void setAccountFeesActionDetail(Set<AccountFeesActionDetail> accountFeesActionDetail)
	{
		if(accountFeesActionDetail != null)
		{
			Iterator<AccountFeesActionDetail> iter = accountFeesActionDetail.iterator();
			while(iter.hasNext())
			{
				AccountFeesActionDetail accountFeesAction = iter.next();
				accountFeesAction.setAccountFeeDetailId(this);
			}
		}
		this.accountFeesActionDetail = accountFeesActionDetail;
	}

	public Set<AccountFeesActionDetail> getAccountFeesActionDetail()
	{
		return accountFeesActionDetail;
	}


	public Short getInstallmentId()
	{
		return installmentId;
	}
/*
	public Integer getAccountId()
	{
		return accountId;
	}
	*/

	public Integer getAccountId()
	{
		return account.getAccountId();
	}
	

	public Account getAccount()
	{
		return account;
	}

	public Integer getCustomerId()
	{
		return customerId;
	}

	public Short getCurrencyId()
	{
		return currencyId;
	}

	public Date getActionDate()
	{
		return actionDate;
	}

	public Money getDeposit()
	{
		return deposit;
	}

	public Money getPrincipal()
	{
		return principal;
	}

	public Money getInterest()
	{
		return interest;
	}

	public Money getPenalty()
	{
		return penalty;
	}

	public Money getDepositPaid()
	{
		return depositPaid;
	}

	public Money getPrincipalPaid()
	{
		return principalPaid;
	}

	public Money getInterestPaid()
	{
		return interestPaid;
	}

	public Money getPenaltyPaid()
	{
		return penaltyPaid;
	}

	public Short getParentFlag()
	{
		return parentFlag;
	}

	public Money getMiscPenaltyPaid() {
		return miscPenaltyPaid;
	}

	public void setMiscPenaltyPaid(Money miscPenaltyPaid) {
		this.miscPenaltyPaid = miscPenaltyPaid;
	}

	public Short getPaymentStatus()
	{
		return paymentStatus;
	}
	public Integer getVersionNo() {
		return versionNo;
	}	

	/**
	 * @param versionNo The versionNo to set.
	 */
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Money getTotalAmount(){
		return getPrincipal().add(getInterest()).add(getPenalty()).add(getTotalFeesAmount());
	}
	
	public Money getTotalFeesAmount(){
		Money amount = new Money();
		if(getAccountFeesActionDetail()!= null  && getAccountFeesActionDetail().size()!=0){
			for(AccountFeesActionDetail feeDetail : getAccountFeesActionDetail())
				amount = amount.add(feeDetail.getFeeAmount());
		}
		return amount;
	}
	
	public Money getTotalFeesAmountForId(Short feeId){
		Money amount = new Money();
		if(getAccountFeesActionDetail()!= null  && getAccountFeesActionDetail().size()!=0){
			for(AccountFeesActionDetail feeDetail : getAccountFeesActionDetail())
				if(feeId.equals(feeDetail.getFeeId()))
					amount = amount.add(feeDetail.getFeeAmount());
		}
		return amount;
	}
	
	public Money getTotalAmountWithMisc(){
		return getPrincipal().add(getInterest()).add(getPenalty()).add(getTotalFeesAmount()).add(getMiscFee()).add(getMiscPenalty());
	}
	
	public Money getTotalFeesAmountWithMisc(){
		return getTotalFeesAmount().add(getMiscFee());
	}

	/**
	 * @return Returns the miscPenalty.
	 */
	public Money getMiscPenalty() 
	{
		return miscPenalty;
	}

	/**
	 * @param miscPenalty The miscPenalty to set.
	 */
	public void setMiscPenalty(Money miscPenalty) {
		this.miscPenalty = miscPenalty;
	}
	public boolean equals(Object obj)
	{
		AccountActionDate accountActionDate = (AccountActionDate)obj;
		if(this.actionDateId.intValue() == accountActionDate.getActionDateId().intValue())
		{
			 return true;
		}
		else 
		{
			return false;
		}
		
	}
	
	
	 public Money getPrincipalDue()
	 {
	  	return getPrincipal().subtract(getPrincipalPaid());
	 }

	 public Money getInterestDue()
	 {
	   	return getInterest().subtract(getInterestPaid());
	 }
	    
	 public Money getPenaltyDue()
	 {
		 return getPenalty().add(getMiscPenalty()).subtract(getPenaltyPaid());
	 }
	public Money getMiscFeeDue() 
	{
		return getMiscFee().subtract(getMiscFeePaid()); 
	}

	 public Money getTotalDue()
	 {
		 return getPrincipalDue().add(getInterestDue()).add(getPenaltyDue()).add(getMiscFeeDue());
	 }
	 
	 public Money getTotalDueWithoutPricipal()
	 {
		 return  getInterestDue().add(getPenaltyDue()).add(getMiscFeeDue());
	 }
	 
	 public Money getMiscPenaltyDue(){
		 return getMiscPenalty().subtract(getMiscPenaltyPaid());
	 }
	 public Money getTotalChargesDue(){
		 Money amount = new Money();
		 amount=amount.add(getMiscFeeDue()).add(getMiscPenaltyDue());
		 if(getAccountFeesActionDetail()!= null  && getAccountFeesActionDetail().size()!=0){
			for(AccountFeesActionDetail feeDetail : getAccountFeesActionDetail())
				amount = amount.add(feeDetail.getFeeDue());
		 }
		 return amount;
	 }
}

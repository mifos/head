package org.mifos.application.accounts.util.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.util.helpers.Money;

public class PaymentData {

	private Money totalAmount;

	private CustomerBO customer;

	private PersonnelBO personnel;

	private Date transactionDate;

	private String recieptNum;

	private Date recieptDate;

	private Short paymentTypeId;

	private List<AccountPaymentData> accountPayments;

    private PaymentData(Money totalAmount, PersonnelBO personnel,
            Short paymentId, Date transactionDate) {
        accountPayments = new ArrayList<AccountPaymentData>();
        setTotalAmount(totalAmount);
        setPersonnel(personnel);
        setPaymentTypeId(paymentId);
        setTransactionDate(transactionDate);
    }

	private PaymentData(Money totalAmount, PersonnelBO personnel,
			Short paymentId, Date transactionDate,
            String recieptNum, Date recieptDate) {
		accountPayments = new ArrayList<AccountPaymentData>();
		setTotalAmount(totalAmount);
		setPersonnel(personnel);
		setPaymentTypeId(paymentId);
		setTransactionDate(transactionDate);
        setRecieptNum(recieptNum);
        setRecieptDate(recieptDate);
    }


    public static PaymentData createPaymentData(Money totalAmount, PersonnelBO personnel,
                                                Short paymentId, Date transactionDate) {
        return new PaymentData(totalAmount, personnel, paymentId, transactionDate);
    }

    public static PaymentData createRecieptPaymentData(
            RecieptPaymentDataTemplate template) {
        return new PaymentData(template.getTotalAmount(), template.getPersonnel(),
                template.getPaymentTypeId(), template.getTransactionDate(),
                template.getPaymentRecieptNumber(), template.getPaymentRecieptDate());
    }

    public static PaymentData createPaymentData(PaymentDataTemplate template) {
        return new PaymentData(template.getTotalAmount(), template.getPersonnel(),
                template.getPaymentTypeId(), template.getTransactionDate());
    }

    public List<AccountPaymentData> getAccountPayments() {
		return accountPayments;
	}

	public Short getPaymentTypeId() {
		return paymentTypeId;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public Date getRecieptDate() {
		return recieptDate;
	}

	public String getRecieptNum() {
		return recieptNum;
	}

	public Money getTotalAmount() {
		return totalAmount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	private void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}

	public CustomerBO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	private void setPaymentTypeId(Short paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public void setRecieptDate(Date recieptDate) {
		this.recieptDate = recieptDate;
	}

	public void setRecieptNum(String recieptNum) {
		this.recieptNum = recieptNum;
	}

	private void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}

	private void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public void addAccountPaymentData(AccountPaymentData accountPaymentData) {
		accountPayments.add(accountPaymentData);
	}
}

/**

 * CustomerHistoricalDataActionForm.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.application.customer.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;

/**
 * This class denotes the form bean for the CustomerHistoricalData.
 * It consists of the fields for which the user inputs values
 */
public class CustomerHistoricalDataActionForm extends MifosActionForm{
	
	/**Denotes the id of the customer whose histrorical data is there.*/	
	private String customerId;
	
	/**Denotes the id of the histrorical data.*/	
	private String historicalId;
	
	/**Denotes the customer mfi joining date*/
	private String mfiJoiningDate;
	
	/**Denotes the product name*/
	private String productName;
	
	/**Denotes the last loan amount availed by customer*/
	private Money loanAmount;
	
	/**Denotes the total amount paid by the customer for the last loan*/
	private Money totalAmountPaid;
	
	/**Denotes the interest paid by the customer*/
	private Money interestPaid;
	
	/**Denotes the count of number of missed payments*/
	private String missedPaymentsCount;
	
	/**Denotes the count of total payments*/
	private String totalPaymentsCount;
	
	/**Denotes the comment*/
	private String notes;
	
	/**Denotes the loan cycle number*/
	private String loanCycleNumber;
	
	/**Denotes the cancelBtn property*/
	private String cancelBtn;
	
	/**Denotes the button value for group jsps*/
	private String btn;
	
	/**Denotes the version no of the group*/
	private String versionNo;

	/**
     * Return the value of the versionNo attribute.
     * @return String
     */
	public String getVersionNo() {
		return versionNo;
	}

	/**
     * Sets the value of versionNo
     * @param versionNo  
     */
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	
	/**
     * Return the value of the cancelBtn attribute.
     * @return String
     */

	public String getCancelBtn() {
		return cancelBtn;
	}
	
	/**
     * Sets the value of canclBtn
     * @param cancelBtn
     */
	public void setCancelBtn(String cancelBtn) {
		this.cancelBtn = cancelBtn;
	}
	
	/**
     * Return the value of the btn attribute.
     * @return String
     */
	public String getBtn() {
		return btn;
	}

	/**
     * Sets the value of the btn attribute.
     * @param btn
     */
	public void setBtn(String btn) {
		this.btn = btn;
	}
	
	/**
     * Return the value of the customerId attribute.
     * @return String
     */
	public String getCustomerId() {
		return customerId;
	}
	
	/**
     * Sets the value of customerId
     * @param customerId
     */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	/**
     * Return the value of the interestPaid attribute.
     * @return String
     */
	public Money getInterestPaid() {
		return interestPaid;
	}
	
	public double getInterestPaidDoubleValue() {
		return interestPaid.getAmountDoubleValue();
	}
	/**
     * Sets the value of interestPaid
     * @param interestPaid
     */
	public void setInterestPaid(Money interestPaid) {
		this.interestPaid = interestPaid;
	}
	
	/**
     * Return the value of the loanAmount attribute.
     * @return String
     */
	public Money getLoanAmount() {
		return loanAmount;
	}
	
	public double getLoanAmountDoubleValue() {
		return loanAmount.getAmountDoubleValue();
	}
	/**
     * Sets the value of loanAmount
     * @param loanAmount
     */
	public void setLoanAmount(Money loanAmount) {
		this.loanAmount = loanAmount;
	}
	
	/**
     * Return the value of the loanCycleNumber attribute.
     * @return String
     */
	public String getLoanCycleNumber() {
		return loanCycleNumber;
	}
	
	/**
     * Sets the value of loanCycleNumber
     * @param loanCycleNumber
     */
	public void setLoanCycleNumber(String loanCycleNumber) {
		this.loanCycleNumber = loanCycleNumber;
	}
	
	/**
     * Return the value of the missedPaymentsCount attribute.
     * @return String
     */
	public String getMissedPaymentsCount() {
		return missedPaymentsCount;
	}
	
	/**
     * Sets the value of missedPaymentsCount
     * @param missedPaymentsCount
     */
	public void setMissedPaymentsCount(String missedPaymentsCount) {
		this.missedPaymentsCount = missedPaymentsCount;
	}
	
	/**
     * Return the value of the notes attribute.
     * @return String
     */
	public String getNotes() {
		return notes;
	}
	
	/**
     * Sets the value of notes
     * @param notes
     */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
     * Return the value of the productName attribute.
     * @return String
     */
	public String getProductName() {
		return productName;
	}
	
	/**
     * Sets the value of productName
     * @param productName
     */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	/**
     * Return the value of the totalAmountPaid attribute.
     * @return String
     */
	public Money getTotalAmountPaid() {
		return totalAmountPaid;
	}
	
	public double getTotalAmountPaidDoubleValue() {
		return totalAmountPaid.getAmountDoubleValue();
	}
	/**
     * Sets the value of totalAmountPaid
     * @param totalAmountPaid
     */
	public void setTotalAmountPaid(Money totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
	}
	
	/**
     * Return the value of the totalPaymentsCount attribute.
     * @return String
     */
	public String getTotalPaymentsCount() {
		return totalPaymentsCount;
	}
	
	/**
     * Sets the value of totalPaymentsCount
     * @param totalPaymentsCount
     */
	public void setTotalPaymentsCount(String totalPaymentsCount) {
		this.totalPaymentsCount = totalPaymentsCount;
	}
	
	
	/**
     * Sets the value of historical id attribute
     * @param historicalId
     */
	public void setHistoricalId(String historicalId)
	{
		this.historicalId = historicalId;
	}
	
	/**
	 * Return the historical id
     * @return String
     */
	public String getHistoricalId()
	{
		return historicalId;
	}

	/**
	 * Method which returns the mfiJoiningDate	
	 * @return Returns the mfiJoiningDate.
	 */
	public String getMfiJoiningDate() {
		return mfiJoiningDate;
	}

	/**
	 * Method which sets the mfiJoiningDate
	 * @param mfiJoiningDate The mfiJoiningDate to set.
	 */
	public void setMfiJoiningDate(String mfiJoiningDate) {
		this.mfiJoiningDate = mfiJoiningDate;
	}
	
	/**
	 * This method is used in addition to validation framework to do input data validations before proceeding.  
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 * @throws ApplicationException
	 */
	public ActionErrors customValidate(ActionMapping mapping, HttpServletRequest request) throws ApplicationException {
		String methodCalled= request.getParameter("method");
		if(null !=methodCalled) {
			if(CustomerConstants.METHOD_CANCEL.equals(methodCalled) || 
			  (CustomerConstants.METHOD_SEARCH).equals(methodCalled)||
			  (CustomerConstants.METHOD_GET).equals(methodCalled)||
			  (CustomerConstants.METHOD_PREVIOUS).equals(methodCalled)||	 
			  (CustomerConstants.METHOD_UPDATE).equals(methodCalled)||
			   CustomerConstants.METHOD_LOAD.equals(methodCalled)){
					request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
		}
		return null;	
	}
}


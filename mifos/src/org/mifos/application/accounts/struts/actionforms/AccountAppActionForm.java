/**
 * 
 */
package org.mifos.application.accounts.struts.actionforms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;




/**
 * @author krishankg
 *
 */
public class AccountAppActionForm extends ValidatorActionForm{
	private String accountId;
	private String customerId;
	private String selectedPrdOfferingId;
	private String stateSelected;
	private String globalAccountNum;
	private String input;
	
	private List<AccountCustomFieldEntity> accountCustomFieldSet;
	
	
	//values for moving back
	private String searchInput;
	private String prdOfferingName;
	private String headingInput;
	private String accountType;
	
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getHeadingInput() {
		return headingInput;
	}

	public void setHeadingInput(String headingInput) {
		this.headingInput = headingInput;
	}

	public String getPrdOfferingName() {
		return prdOfferingName;
	}

	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

	public String getSearchInput() {
		return searchInput;
	}

	public void setSearchInput(String searchInput) {
		this.searchInput = searchInput;
	}

	public AccountAppActionForm(){
		accountCustomFieldSet= new ArrayList<AccountCustomFieldEntity>();
	}
	
	public String getStateSelected() {
		return stateSelected;
	}
	
	public void setStateSelected(String stateSelected) {
		this.stateSelected = stateSelected;
	}
	
	public String getSelectedPrdOfferingId() {
		return selectedPrdOfferingId;
	}
	
	public void setSelectedPrdOfferingId(String selectedPrdOfferingId) {
		this.selectedPrdOfferingId = selectedPrdOfferingId;
	}
	
	public String getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
		
	public Set<AccountCustomFieldEntity> getAccountCustomFieldSet(){
		return new HashSet<AccountCustomFieldEntity>(this.accountCustomFieldSet);
	}
	
	private void setAccountCustomFieldSet(List<AccountCustomFieldEntity> accountCustomFieldSet) {
		this.accountCustomFieldSet = accountCustomFieldSet;
	}
	
	public String getGlobalAccountNum() {
		return globalAccountNum;
	}
	
	public void setGlobalAccountNum(String globalAccountNumber) {
		this.globalAccountNum = globalAccountNumber;
	}
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}	
	
	/**
 	* Return the value of the one of the custom field at index i in accountCustomFields List.
 	*/
	public AccountCustomFieldEntity getCustomField(int i){
		while(i>=accountCustomFieldSet.size()){
			accountCustomFieldSet.add(new AccountCustomFieldEntity ());
		}
		return (AccountCustomFieldEntity)(accountCustomFieldSet.get(i));
	}

}

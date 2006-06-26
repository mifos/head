package org.mifos.application.fund.util.valueobjects;

import java.util.Comparator;

import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.master.util.valueobjects.GLCode;
import org.mifos.framework.util.valueobjects.ValueObject;

public class Fund extends ValueObject {

	public Fund(){
		glCode = new GLCode();
	}
	
	/**Denotes the fund id*/
	private Short fundId;
	/**Denotes the glcode associated for the fund*/
	private GLCode glCode;
	/**Denotes the name for the fund*/
	private String fundName;
	/**Denotes the version number of the fund*/
	private Integer versionNo;
	
	
	/**
	 * @return Returns the fundId.
	 */
	public Short getFundId() {
		return fundId;
	}
	/**
	 * @param fundId The fundId to set.
	 */
	public void setFundId(Short fundId) {
		this.fundId = fundId;
	}
	/**
	 * @return Returns the fundName.
	 */
	public String getFundName() {
		return fundName;
	}
	/**
	 * @param fundName The fundName to set.
	 */
	public void setFundName(String fundName) {
		this.fundName = fundName;
	}
	/**
	 * @return Returns the glCode.
	 */
	public GLCode getGlCode() {
		return glCode;
	}
	/**
	 * @param glCode The glCode to set.
	 */
	public void setGlCode(GLCode glCode) {
		this.glCode = glCode;
	}
	/**
	 * @return Returns the versionNo.
	 */
	public Integer getVersionNo() {
		return versionNo;
	}
	/**
	 * @param versionNo The versionNo to set.
	 */
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	
	public String getResultName(){
		return FundConstants.FUNDVO;
	}
	
/**
	 * This method has been overridden to perform comparision while searching a set of Fund objects.
	 * The business key here is fundId 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		Fund fund = (Fund)obj;
		
		if(this.fundId.equals(fund.getFundId())){
				return true;
		}else{
			return false;
		}
		
	}
	
}

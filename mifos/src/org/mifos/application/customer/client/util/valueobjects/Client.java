package org.mifos.application.customer.client.util.valueobjects;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerNameDetail;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;

/**
 * This class acts as valueObject for clients module
 * @author ashishsm
 *
 */
public class Client extends Customer {
	
	public Client(){
		this.customerNameDetailSet = new HashSet();
		logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	}
	/**An insatnce of the logger which is used to log statements */
	private MifosLogger logger ;
	/**This variable indicates if client being created is part of group or not.*/
	private short isClientUnderGrp;
	
	/**This variable indicates the id of the group under which the  client is being created */
	private int parentGroupId;
	private InputStream customerPicture;
	private Set customerNameDetailSet;
	
	private short flagId;
	
	private ClientPerformanceHistory clientPerformanceHistory;
	/**
	 * Method which returns the flagId	
	 * @return Returns the flagId.
	 */
	public short getFlagId() {
		return flagId;
	}

	/**
	 * Method which sets the flagId
	 * @param flagId The flagId to set.
	 */
	public void setFlagId(short flagId) {
		this.flagId = flagId;
	}

	/**
	 * Method which returns the customerNameDetailSet	
	 * @return Returns the customerNameDetailSet.
	 */
	public Set getCustomerNameDetailSet() {
		return customerNameDetailSet;
	}

	/**
	 * Method which sets the customerNameDetailSet
	 * @param customerNameDetailSet The customerNameDetailSet to set.
	 */
	public void setCustomerNameDetailSet(Set customerNameDetailSet) {
		if(customerNameDetailSet != null){
			for(Object obj : customerNameDetailSet){
				((CustomerNameDetail)obj).setCustomer(this);
			}
		}
		this.customerNameDetailSet = customerNameDetailSet;
	}

	
	/**
	 * Method which returns the isClientUnderGrp	
	 * @return Returns the isClientUnderGrp.
	 */
	public short getIsClientUnderGrp() {
		return isClientUnderGrp;
	}

	/**
	 * Method which sets the isClientUnderGrp
	 * @param isClientUnderGrp The isClientUnderGrp to set.
	 */
	public void setIsClientUnderGrp(short isClientUnderGrp) {
		this.isClientUnderGrp = isClientUnderGrp;
	}

	/**
	 * @return Returns the customerPicture}.
	 */
	public InputStream getCustomerPicture() {
		return customerPicture;
	}

	/**
	 * @param customerPicture The customerPicture to set.
	 */
	public void setCustomerPicture(InputStream customerPicture) {
		this.customerPicture = customerPicture;
	}
	public String getResultName(){
		return ClientConstants.CLIENTVO;
	}
	public void printClientDetails(){
		logger.debug("----Client Id: " +getCustomerId());
		logger.debug("----Client Name: "+getDisplayName());
		logger.debug("----Client Office: "+getOffice());
		logger.debug("----Client office Version: "+getOffice().getVersionNo());
		logger.debug("----Client GlobalCustNum: "+getGlobalCustNum());
		logger.debug("----Client Id: " +getStatusId());
		logger.debug("----Client Address: "+getDisplayAddress());
		logger.debug("----Client ExternalId: "+getExternalId());
		logger.debug("----Client Personnel: "+getPersonnel().getDisplayName());
		logger.debug("----Client SeatchId : "+getSearchId());
		logger.debug("----Client Gender: "+getCustomerDetail().getGender());
		logger.debug("----Client Handicapped: "+getCustomerDetail().getHandicapped());
		logger.debug("----Client BusinessActivities: "+getCustomerDetail().getBusinessActivities());
		logger.debug("----Client Ethinicity: "+getCustomerDetail().getEthinicity());
		logger.debug("----Client EducationLevel: "+getCustomerDetail().getEducationLevel());
		
		
	}

	/**
	 * Method which returns the parentGroupId	
	 * @return Returns the parentGroupId.
	 */
	public int getParentGroupId() {
		return parentGroupId;
	}

	/**
	 * Method which sets the parentGroupId
	 * @param parentGroupId The parentGroupId to set.
	 */
	public void setParentGroupId(int parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	public ClientPerformanceHistory getClientPerformanceHistory() {
		return clientPerformanceHistory;
	}

	public void setClientPerformanceHistory(
			ClientPerformanceHistory clientPerformanceHistory) {
		this.clientPerformanceHistory = clientPerformanceHistory;
	}
	

	
}

/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.customer.client.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.ClientTemplate;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.StringUtils;

public class ClientPersistence extends Persistence {

    private CustomerPersistence customerPersistence = new CustomerPersistence();
    private OfficePersistence officePersistence = new OfficePersistence();
    private PersonnelPersistence personnelPersistence = new PersonnelPersistence();

    public ClientBO createClient(UserContext userContext, ClientTemplate template)
            throws CustomerException, PersistenceException, ValidationException {
        CustomerBO parentCustomer = null;
        if (template.getParentCustomerId() != null) {
            parentCustomer = getCustomerPersistence()
                .getCustomer(template.getParentCustomerId());
            if (parentCustomer == null) {
                throw new ValidationException(CustomerConstants.INVALID_PARENT);
            }
        }
        
        ClientBO client = new ClientBO(userContext, template.getDisplayName(),
                template.getCustomerStatus(), template.getExternalId(),
                template.getMfiJoiningDate(), template.getAddress(),
                template.getCustomFieldViews(), template.getFees(),
                template.getOfferingsSelected(), 
                personnelPersistence.getPersonnel(template.getFormedById()),
                officePersistence.getOffice(template.getOfficeId()), parentCustomer,
                template.getDateOfBirth(), template.getGovernmentId(),
                template.getTrained(), template.getTrainedDate(),
                template.getGroupFlag(), template.getClientNameDetailView(),
                template.getSpouseNameDetailView(),
                template.getClientDetailView(), template.getPicture());
        customerPersistence.saveCustomer(client);
        return client;
    }

    /**
     * Get a client by Id and inject any required dependencies
     */
    public ClientBO getClient(Integer customerId) throws PersistenceException{
        return (ClientBO) getPersistentObject(ClientBO.class,customerId);
	}
	
    // Returns true if another client with same govt id is found with a state other than closed
	public boolean checkForDuplicacyOnGovtIdForNonClosedClients(
			String governmentId, Integer customerId)
			throws PersistenceException {
		return checkForClientsBasedOnGovtId(
				NamedQueryConstants.GET_NON_CLOSED_CLIENT_BASEDON_GOVTID,
				governmentId, customerId);
	}

	// Integer.valueOf(0) because of the way query is written in CustomerBO.hbm.xml
	// Returns true if another client in closed state with same govt id is found
	public boolean checkForDuplicacyOnGovtIdForClosedClients(String governmentId) throws PersistenceException {
		return checkForClientsBasedOnGovtId(
				NamedQueryConstants.GET_CLOSED_CLIENT_BASEDON_GOVTID,
				governmentId, Integer.valueOf(0));
	}
	
	private boolean checkForClientsBasedOnGovtId(String queryName,
			String governmentId, Integer customerId)
			throws PersistenceException {
		
		// if government id is null or empty, do not match against closed client's government id, doesn't make sense 
		if(StringUtils.isNullOrEmpty(governmentId)) return false;
		
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LEVEL_ID", CustomerLevel.CLIENT.getValue());
		queryParameters.put("GOVT_ID", governmentId);
		queryParameters.put("customerId", customerId);
		queryParameters.put("clientStatus", CustomerStatus.CLIENT_CLOSED
				.getValue());
		List queryResult = executeNamedQuery(queryName, queryParameters);
		return ((Long) queryResult.get(0)).intValue() > 0;
	}
	
	// returns true if a duplicate client is found with same display name and dob in state other than closed 
	public boolean checkForDuplicacyForNonClosedClientsOnNameAndDob(String name,
			Date dob, Integer customerId) throws PersistenceException {
		return checkForDuplicacyBasedOnName(
				NamedQueryConstants.GET_NON_CLOSED_CLIENT_BASED_ON_NAME_DOB, name,
				dob, customerId);
	}

	// returns true if a duplicate client is found with same display name and dob in closed state
	public boolean checkForDuplicacyForClosedClientsOnNameAndDob(String name,
			Date dob) throws PersistenceException {
		return checkForDuplicacyBasedOnName(
				NamedQueryConstants.GET_CLOSED_CLIENT_BASED_ON_NAME_DOB, name,
				dob, Integer.valueOf(0));
	}
	
	private boolean checkForDuplicacyBasedOnName(String queryName, String name,
			Date dob, Integer customerId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("clientName", name);
		queryParameters.put("LEVELID", CustomerLevel.CLIENT.getValue());
		queryParameters.put("DATE_OFBIRTH", dob);
		queryParameters.put("customerId", customerId);
		queryParameters.put("clientStatus", CustomerStatus.CLIENT_CLOSED
				.getValue());
		List queryResult = executeNamedQuery(
				queryName,
				queryParameters);
		return ((Number) queryResult.get(0)).intValue() > 0;
	}

	public Blob createBlob(InputStream picture) throws PersistenceException{
		try{
			return Hibernate.createBlob(picture);
		}
		catch(IOException ioe){
			throw new PersistenceException(ioe);
		}
	}
	
	public List<SavingsOfferingBO> retrieveOfferingsApplicableToClient()
	throws PersistenceException{
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("prdApplicableTo",
				ApplicableTo.CLIENTS.getValue());
		return executeNamedQuery(
				NamedQueryConstants.GET_ACTIVE_OFFERINGS_FOR_CUSTOMER, 
				queryParameters);
	}	
	
	public List<ClientBO> getActiveClientsUnderParent(String searchId,
			Short officeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", searchId + ".%");
		queryParameters.put("OFFICE_ID", officeId);
		List<ClientBO> queryResult = executeNamedQuery(
				NamedQueryConstants.ACTIVE_CLIENTS_UNDER_PARENT,
				queryParameters);
		return queryResult;
	}
		public List<ClientBO> getActiveClientsUnderGroup(Integer groupId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("GROUP_ID", groupId.intValue());
		List<ClientBO> queryResult = executeNamedQuery(
				NamedQueryConstants.ACTIVE_CLIENTS_UNDER_GROUP, queryParameters);
		return queryResult;
	}
    public CustomerPersistence getCustomerPersistence() {
        return customerPersistence;
    }

    public OfficePersistence getOfficePersistence() {
        return officePersistence;
    }
    
    public void saveClient(ClientBO clientBO) throws CustomerException {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        customerPersistence.saveCustomer(clientBO);
        try {
            if (clientBO.getParentCustomer() != null)
                customerPersistence.createOrUpdate(clientBO
                        .getParentCustomer());
            // seems fishy... why do savings accounts need updating here?
            new SavingsPersistence().persistSavingAccounts(clientBO);
        } catch (PersistenceException pe) {
            throw new CustomerException(
                    CustomerConstants.CREATE_FAILED_EXCEPTION, pe);
        }
    }
}

/**

 * CustomerNameDetail.java    version: xxx



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

package org.mifos.application.customer.client.business;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.business.util.Name;

public class ClientNameDetailEntity extends PersistentObject {

	// Set/gotten by Hibernate
	private final Integer customerNameId;

	private final ClientBO client;

	private Short nameType;

	/* 47=Mr, 48=Mrs, 228=Ms (is this right?)*/
	private Integer salutation;

	private String secondMiddleName;

	private String displayName;

	private Name name;

	public ClientNameDetailEntity(ClientBO client, String secondMiddleName, ClientNameDetailView view) {
		super();
		this.customerNameId = null;
		this.client = client;
		this.nameType = view.getNameType();
		this.salutation = view.getSalutation();
		this.secondMiddleName = secondMiddleName;
		this.displayName = view.getDisplayName();
		this.name = view.asName();
	}

	protected ClientNameDetailEntity() {
		super();
		this.customerNameId = null;
		this.client = null;
		this.nameType = null;
		this.salutation = null;
		this.secondMiddleName = null;
		this.displayName = null;
		this.name = null;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Integer getSalutation() {
		return salutation;
	}

	public void setSalutation(Integer salutation) {
		this.salutation = salutation;
	}

	public String getSecondMiddleName() {
		return secondMiddleName;
	}

	public void setSecondMiddleName(String secondMiddleName) {
		this.secondMiddleName = secondMiddleName;
	}

	public CustomerBO getClient() {
		return client;
	}

	public Short getNameType() {
		return this.nameType;
	}

	public void setNameType(Short nameType) {
		this.nameType = nameType;
	}

	public Integer getCustomerNameId() {
		return customerNameId;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public void updateNameDetails(ClientNameDetailView nameView) {
		this.nameType = nameView.getNameType();
		this.salutation = nameView.getSalutation();
		//this.secondMiddleName = what?;
		this.displayName = nameView.getDisplayName();
		this.name = nameView.asName();
	}

}

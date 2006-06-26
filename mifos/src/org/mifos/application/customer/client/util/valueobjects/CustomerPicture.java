/**

 * CustomerPicture.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.application.customer.client.util.valueobjects;

import java.sql.Blob;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class maps to customer_picture table of the database, which is used to store
 * customer picture as a blob.
 * @author ashishsm
 *
 */
public class CustomerPicture extends ValueObject {

	/**
	 * 
	 */
	public CustomerPicture() {
		super();
		
	}
	private Integer pictureId;
	private Integer customerId;
	
	private Blob picture;

	/**
	 * @return Returns the customerId}.
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return Returns the picture}.
	 */
	public Blob getPicture() {
		return picture;
	}

	/**
	 * @param picture The picture to set.
	 */
	public void setPicture(Blob picture) {
		this.picture = picture;
	}

	/**
	 * Method which returns the pictureId	
	 * @return Returns the pictureId.
	 */
	public Integer getPictureId() {
		return pictureId;
	}

	/**
	 * Method which sets the pictureId
	 * @param pictureId The pictureId to set.
	 */
	public void setPictureId(Integer pictureId) {
		this.pictureId = pictureId;
	}

}

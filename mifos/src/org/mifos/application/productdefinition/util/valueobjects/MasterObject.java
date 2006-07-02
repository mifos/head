/**

 * MasterObject.java    version: xxx

 

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
package org.mifos.application.productdefinition.util.valueobjects;

import java.io.Serializable;

/**
 * This is a helper class which would be used to retrieve master data when we need only certain details of
 * the object hence instead of loading the entire object we can load only this object using a query.
 * 
 * @author mohammedn
 *
 */
public class MasterObject implements Serializable {
	/**
	 *Default Constructor 
	 */
	public MasterObject() {
		super();
	}
	/**
	 * Constructor 
	 * @param id
	 * @param name
	 */
	public MasterObject(Short id,String name) {
		super();
		this.id=id;
		this.name=name;
	}
	/**
	 * Constructor 
	 * @param id
	 * @param name
	 * @param versionNo
	 */
	public MasterObject(Short id,String name,Integer versionNo) {
		super();
		this.id=id;
		this.name=name;
		this.versionNo=versionNo;
	}
	/**
	 * id of the object
	 */
	private Short id;
	/**
	 * display name of the object
	 */
	private String name;
	/**
	 * version no,if any, of the object
	 */
	private Integer versionNo;

	/**
	 * @return Returns the id.
	 */
	public Short getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(Short id) {
		this.id = id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
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
	

}

/**

 * CheckListDetail.java    version: 1.0

 

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

package org.mifos.application.checklist.util.valueobjects;

import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * A class that represents a row in the 'checklist_detail' table. 
 * This class may be customized as it is never re-generated 
 * after being created.
 */
public class CheckListDetail extends ValueObject {
	/**
	 * Simple constructor of ChecklistDetail instances.
	 */
	public CheckListDetail() {
	}

	/** The composite primary key value. */
	private Integer detailId;

	/** The value of the supportedLocale association. */
	private SupportedLocales supportedLocales;

	/** The value of the checklist association. */
	private CheckList checkList;

	/** The value of the simple detailText property. */
	private String detailText;

	/** The value of the simple answerType property. */
	private Short answerType;

	/**
	 * Return the simple primary key value that identifies this object.
	 * @return  Integer
	 */
	public Integer getDetailId() {
		return detailId;
	}

	/**
	 * Set the simple primary key value that identifies this object.
	 * @param detailId
	 */
	public void setDetailId(Integer detailId) {

		this.detailId = detailId;
	}

	/**
	 * Return the value of the CHECKLIST_ID column.
	 * @return Checklist
	 */
	public CheckList getCheckList() {
		return this.checkList;
	}

	/**
	 * Set the value of the CHECKLIST_ID column.
	 * @param checklist
	 */
	public void setCheckList(CheckList checkList) {
		this.checkList = checkList;
	}

	/**
	 * Return the value of the LOCALE_ID column.
	 * @return SupportedLocale
	 */
	public SupportedLocales getSupportedLocales() {
		return this.supportedLocales;
	}

	/**
	 * Set the value of the LOCALE_ID column.
	 * @param supportedLocale
	 */
	public void setSupportedLocales(SupportedLocales supportedLocales) {
		this.supportedLocales = supportedLocales;
	}

	/**
	 * Return the value of the DETAIL_TEXT column.
	 * @return  String
	 */
	public String getDetailText() {
		return this.detailText;
	}

	/**
	 * Set the value of the DETAIL_TEXT column.
	 * @param detailText
	 */
	public void setDetailText(String detailText) {
		this.detailText = detailText;
	}

	/**
	 * Return the value of the ANSWER_TYPE column.
	 * @return  Short
	 */
	public Short getAnswerType() {
		return this.answerType;
	}

	/**
	 * Set the value of the ANSWER_TYPE column.
	 * @param answerType
	 */
	public void setAnswerType(Short answerType) {
		this.answerType = answerType;
	}
	
	

}

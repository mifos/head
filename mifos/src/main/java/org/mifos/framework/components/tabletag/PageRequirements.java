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
 
package org.mifos.framework.components.tabletag;

public class PageRequirements {

	public PageRequirements() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	private String numbersRequired;
	private String headingRequired;
	private String bluelineRequired;
	private String topbluelineRequired;
	private String bottombluelineRequired;
	private String valignnumbers;
	private String blanklinerequired;
	private String flowRequired;
	
	/**
	 * @return Returns the bluelineRequired.
	 */
	public String getBluelineRequired() {
		return bluelineRequired;
	}
	/**
	 * @param bluelineRequired The bluelineRequired to set.
	 */
	public void setBluelineRequired(String bluelineRequired) {
		this.bluelineRequired = bluelineRequired;
	}
	/**
	 * @return Returns the headingRequired.
	 */
	public String getHeadingRequired() {
		return headingRequired;
	}
	/**
	 * @param headingRequired The headingRequired to set.
	 */
	public void setHeadingRequired(String headingRequired) {
		this.headingRequired = headingRequired;
	}
	/**
	 * @return Returns the numbersRequired.
	 */
	public String getNumbersRequired() {
		return numbersRequired;
	}
	/**
	 * @param numbersRequired The numbersRequired to set.
	 */
	public void setNumbersRequired(String numbersRequired) {
		this.numbersRequired = numbersRequired;
	}
	/**
	 * @return Returns the valignnumbers.
	 */
	public String getValignnumbers() {
		return valignnumbers;
	}
	/**
	 * @param valignnumbers The valignnumbers to set.
	 */
	public void setValignnumbers(String valignnumbers) {
		this.valignnumbers = valignnumbers;
	}
	/**
	 * @return Returns the blanklinerequired.
	 */
	public String getBlanklinerequired() {
		return blanklinerequired;
	}
	/**
	 * @param blanklinerequired The blanklinerequired to set.
	 */
	public void setBlanklinerequired(String blanklinerequired) {
		this.blanklinerequired = blanklinerequired;
	}

	/**
	 * @return Returns the topbluelineRequired.
	 */
	public String getTopbluelineRequired() {
		return topbluelineRequired;
	}
	/**
	 * @param topbluelineRequired The topbluelineRequired to set.
	 */
	public void setTopbluelineRequired(String topbluelineRequired) {
		this.topbluelineRequired = topbluelineRequired;
	}
	/**
	 * @return Returns the bottombluelineRequired.
	 */
	public String getBottombluelineRequired() {
		return bottombluelineRequired;
	}
	/**
	 * @param bottombluelineRequired The bottombluelineRequired to set.
	 */
	public void setBottombluelineRequired(String bottombluelineRequired) {
		this.bottombluelineRequired = bottombluelineRequired;
	}
	
	public String getFlowRequired() {
		return flowRequired;
	}
	
	public void setFlowRequired(String flowRequired) {
		this.flowRequired = flowRequired;
	}
	
}

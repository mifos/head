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

public class Fragment {

	public Fragment() {
	}
	
	/** Used to set the value of fragmentName */
	private String fragmentName;

	/** Used to set the value of fragmentType */
	private String fragmentType;

/** Used to set the value of fragmentName */
	private String bold;

	/** Used to set the value of fragmentType */
	private String italic;

	/**
	 * @return Returns the bold.
	 */
	public String getBold() {
		return bold;
	}

	/**
	 * @param bold The bold to set.
	 */
	public void setBold(String bold) {
		this.bold = bold;
	}

	/**
	 * @return Returns the fragmentName.
	 */
	public String getFragmentName() {
		return fragmentName;
	}

	/**
	 * @param fragmentName The fragmentName to set.
	 */
	public void setFragmentName(String fragmentName) {
		this.fragmentName = fragmentName;
	}

	/**
	 * @return Returns the fragmentType.
	 */
	public String getFragmentType() {
		return fragmentType;
	}

	/**
	 * @param fragmentType The fragmentType to set.
	 */
	public void setFragmentType(String fragmentType) {
		this.fragmentType = fragmentType;
	}

	/**
	 * @return Returns the italic.
	 */
	public String getItalic() {
		return italic;
	}

	/**
	 * @param italic The italic to set.
	 */
	public void setItalic(String italic) {
		this.italic = italic;
	}

}

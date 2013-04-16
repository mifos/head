/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.dto.domain;

import java.io.Serializable;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class GLCodeDto implements Serializable {

	private int sno;
    private Short glcodeId;
    private String glcode;
    private String glname;
    private String coaName;
    private String trannotes;
    private String amounts;
    private String accountHead;
    

	public int getSno() {
		return sno;
	}
	public Short getGlcodeId() {
		return glcodeId;
	}
	public String getGlcode() {
		return glcode;
	}
	public String getGlname() {
		return glname;
	}
	public String getCoaName() {
		return coaName;
	}
	public String getTrannotes() {
		return trannotes;
	}
	public String getAmounts() {
		return amounts;
	}
	public String getAccountHead() {
		return accountHead;
	}
	public void setSno(int sno) {
		this.sno = sno;
	}
	public void setGlcodeId(Short glcodeId) {
		this.glcodeId = glcodeId;
	}
	public void setGlcode(String glcode) {
		this.glcode = glcode;
	}
	public void setGlname(String glname) {
		this.glname = glname;
	}
	public void setCoaName(String coaName) {
		this.coaName = coaName;
	}
	public void setTrannotes(String trannotes) {
		this.trannotes = trannotes;
	}
	public void setAmounts(String amounts) {
		this.amounts = amounts;
	}
	public void setAccountHead(String accountHead) {
		this.accountHead = accountHead;
	}




}

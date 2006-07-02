/**

 * FeeMaster.java    version: xxx



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
package org.mifos.application.fees.util.valueobjects;

import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author sumeethaec
 *
 */
public class FeeMaster extends ValueObject{
	
    private Short feeId;

    
    private String feeName;

    
    private Double rateOrAmount;

    private String globalFeeNum;
    private Short checkedFee;
    private Short feeFrequencyTypeId;
    private Integer versionNo;
    private Integer meetingId;
    private Meeting feeMeeting =null ;
	
	public Short getCheckedFee() {
		return checkedFee;
	}
	
	public void setCheckedFee(Short checkedFee) {
		this.checkedFee = checkedFee;
	}
	public FeeMaster(){
		
	}
    public FeeMaster(Short feeId, String feeName , Double rateOrAmount, String globalFeeNum){
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.globalFeeNum = globalFeeNum;
	}
    
    
    public FeeMaster(Short feeId, String feeName , Double rateOrAmount, String globalFeeNum,Integer versionNo){
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.globalFeeNum = globalFeeNum;
		this.versionNo = versionNo;
	}
    public FeeMaster(Short feeId, String feeName , Double rateOrAmount, String globalFeeNum,Integer versionNo,Short feeFrequencyTypeId){
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.globalFeeNum = globalFeeNum;
		this.versionNo = versionNo;
		this.feeFrequencyTypeId=feeFrequencyTypeId;
	}
    public FeeMaster(Short feeId, String feeName , Double rateOrAmount, String globalFeeNum,Integer versionNo,Short feeFrequencyTypeId,Integer meetingId){
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.globalFeeNum = globalFeeNum;
		this.versionNo = versionNo;
		this.feeFrequencyTypeId=feeFrequencyTypeId;
		this.meetingId=meetingId;
	}
    
	public Short getFeeId() {
		return feeId;
	}

	public void setFeeId(Short feeId) {
		this.feeId = feeId;
	}

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public Double getRateOrAmount() {
		return rateOrAmount;
	}

	public double getRateOrAmountDoubleValue() {
		if (rateOrAmount!=null)
			return rateOrAmount;
		else return 0;
	}
	
	public void setRateOrAmount(Double rateOrAmount) {
		this.rateOrAmount = rateOrAmount;
	}

	public String getGlobalFeeNum() {
		return globalFeeNum;
	}
	
	public void setGlobalFeeNum(String globalFeeNum) {
		this.globalFeeNum = globalFeeNum;
	}

	public boolean equals(Object obj)
	{

		FeeMaster feeMaster = (FeeMaster)obj;
		if(feeId.shortValue() == feeMaster.getFeeId().shortValue())
		     return true;

		 return false;
	}
	
	public Integer getVersionNo() {
		return versionNo;
	}
	
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	
	public Short getFeeFrequencyTypeId() {
		return feeFrequencyTypeId;
	}
	
	public void setFeeFrequencyTypeId(Short feeFrequencyTypeId) {
		this.feeFrequencyTypeId = feeFrequencyTypeId;
	}
	
	public Meeting getFeeMeeting() {
		return feeMeeting;
	}
	
	public void setFeeMeeting(Meeting feeMeeting) {
		this.feeMeeting = feeMeeting;
	}
	
	public Integer getMeetingId() {
		return meetingId;
	}
	
	public void setMeetingId(Integer meetingId) {
		this.meetingId = meetingId;
	}
}

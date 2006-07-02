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
package org.mifos.application.fees.util.helpers;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.View;

/**
 * @author sumeethaec
 * 
 */
public class FeeMasterView extends View {

	private Short feeId;

	private String feeName;

	private Double rateOrAmount;

	private String globalFeeNum;

	private Short checkedFee;

	private Short feeFrequencyTypeId;

	private Integer versionNo;

	private Integer meetingId;

	private MeetingBO feeMeeting;

	public Short getCheckedFee() {
		return checkedFee;
	}

	public void setCheckedFee(Short checkedFee) {
		this.checkedFee = checkedFee;
	}

	public FeeMasterView() {

	}

	public FeeMasterView(Short feeId, String feeName, Double rateOrAmount,
			String globalFeeNum) {
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.globalFeeNum = globalFeeNum;
	}

	public FeeMasterView(Short feeId, String feeName, Double rateOrAmount,
			String globalFeeNum, Integer versionNo) {
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.globalFeeNum = globalFeeNum;
		this.versionNo = versionNo;
	}

	public FeeMasterView(Short feeId, String feeName, Double rateOrAmount,
			String globalFeeNum, Integer versionNo, Short feeFrequencyTypeId) {
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.globalFeeNum = globalFeeNum;
		this.versionNo = versionNo;
		this.feeFrequencyTypeId = feeFrequencyTypeId;
	}

	public FeeMasterView(Short feeId, String feeName, Double rateOrAmount,
			String globalFeeNum, Integer versionNo, Short feeFrequencyTypeId,
			Integer meetingId) {
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.globalFeeNum = globalFeeNum;
		this.versionNo = versionNo;
		this.feeFrequencyTypeId = feeFrequencyTypeId;
		this.meetingId = meetingId;
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

	public void setRateOrAmount(Double rateOrAmount) {
		this.rateOrAmount = rateOrAmount;
	}

	public String getGlobalFeeNum() {
		return globalFeeNum;
	}

	public void setGlobalFeeNum(String globalFeeNum) {
		this.globalFeeNum = globalFeeNum;
	}

	public boolean equals(Object obj) {

		FeeMasterView feeMaster = (FeeMasterView) obj;
		if (feeId.shortValue() == feeMaster.getFeeId().shortValue())
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

	public MeetingBO getFeeMeeting() {
		return feeMeeting;
	}

	public void setFeeMeeting(MeetingBO feeMeeting) {
		this.feeMeeting = feeMeeting;
	}

	public Integer getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(Integer meetingId) {
		this.meetingId = meetingId;
	}
}

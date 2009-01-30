/**

 * AccountFeesActionDetailEntity.java    version: 1.0

 

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

package org.mifos.application.bulkentry.business;

import org.mifos.application.fees.business.FeeBO;
import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class CollectionSheetEntryAccountFeeActionView extends View {

	private final Integer actionDateId;

	private final FeeBO fee;

	private final Money feeAmount;

	private final Money feeAmountPaid;

	public CollectionSheetEntryAccountFeeActionView(Integer actionDateId, FeeBO fee,
			Money feeAmount, Money feeAmountPaid) {
		this.actionDateId = actionDateId;
		this.fee = fee;
		this.feeAmount = feeAmount;
		this.feeAmountPaid = feeAmountPaid;
	}

	public CollectionSheetEntryAccountFeeActionView(Integer actionDateId) {
		this(actionDateId, null, null, null);
	}

	public Integer getActionDateId() {
		return actionDateId;
	}

	public FeeBO getFee() {
		return fee;
	}

	public Money getFeeAmount() {
		return feeAmount;
	}

	public Money getFeeAmountPaid() {
		return feeAmountPaid;
	}

	public Money getFeeDue() {
		return getFeeAmount().subtract(getFeeAmountPaid());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CollectionSheetEntryAccountFeeActionView) {
			CollectionSheetEntryAccountFeeActionView otherView = 
				(CollectionSheetEntryAccountFeeActionView) obj;
			if (otherView.getActionDateId().equals(
					getActionDateId()))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		//null case, should never happen, but some one could put in a null constructor
		// (or just pass null to the constructor we have...)
		if(null == this.getActionDateId()){
			return (super.hashCode());
//			throw new NullPointerException("shouldn't happen");
		}
		return this.getActionDateId().intValue();
	}

}

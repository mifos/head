package org.mifos.application.customer.business;

import org.mifos.framework.business.View;

public class CustomerPositionView extends View {
	Integer customerId;

	Short positionId;

	public CustomerPositionView() {
	}

	public CustomerPositionView(Integer customerId, Short positionId) {

		this.customerId = customerId;

		this.positionId = positionId;
	}	

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Short getPositionId() {
		return positionId;
	}

	public void setPositionId(Short positionId) {
		System.out.println("--------------in  set position");
		this.positionId = positionId;
	}

}

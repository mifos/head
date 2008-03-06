package org.mifos.application.checklist.util.helpers;

import java.io.Serializable;
import org.mifos.application.master.MessageLookup;

public class CheckListMasterView implements Serializable {

	private boolean isCustomer;

	private String lookupKey;

	private Short masterTypeId;

	public CheckListMasterView(Short id, String lookupKey) {
		this.masterTypeId = id;
		this.lookupKey = lookupKey;
	}

	public Short getMasterTypeId() {
		return masterTypeId;
	}

	public String getMasterTypeName() {
		if (isCustomer)
			return MessageLookup.getInstance().lookupLabel(lookupKey);
		else
			return MessageLookup.getInstance().lookup(lookupKey);


	}

	public boolean getIsCustomer() {
		return isCustomer;
	}

	public void setIsCustomer(boolean isCustomer) {
		this.isCustomer = isCustomer;
	}
}

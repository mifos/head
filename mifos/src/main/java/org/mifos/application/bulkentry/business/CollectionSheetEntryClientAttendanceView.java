package org.mifos.application.bulkentry.business;

import java.util.Date;

import org.mifos.framework.business.View;



public class CollectionSheetEntryClientAttendanceView extends View {

    private Short attendance;
    
    private final Integer customerId;
    
    private final Date actionDate;

	
	public CollectionSheetEntryClientAttendanceView( Integer customerId,
			Short attendance, Date actionDate) 
    {
        this.attendance = attendance;
        this.actionDate = actionDate;
        this.customerId = customerId;
	
	}

	
	public Short getAttendance() {
		return attendance;
	}

    public Date getActionDate() {
        return actionDate;
    }

  

    public Integer getCustomerId() {
        return customerId;
    }

	

}

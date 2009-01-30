package org.mifos.application.bulkentry.util.helpers;

import java.sql.Date;
import java.util.List;

import org.mifos.application.bulkentry.business.CollectionSheetEntryView;
import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class BulkEntryClientAttendanceThread implements Runnable {

	List<CollectionSheetEntryView> customerViews;

	List<ClientBO> clients;

	List<String> customerNames;

	Date meetingDate;

	StringBuffer isThreadDone;

	public BulkEntryClientAttendanceThread(List<CollectionSheetEntryView> customerViews,
			List<ClientBO> clients, List<String> customerNames,
			Date meetingDate, StringBuffer isThreadDone) {
		this.customerViews = customerViews;
		this.clients = clients;
		this.customerNames = customerNames;
		this.meetingDate = meetingDate;
		this.isThreadDone = isThreadDone;
	}

	public void run() {
		try {
			BulkEntryBusinessService bulkEntryBusinessService = new BulkEntryBusinessService();
			for (CollectionSheetEntryView parent : customerViews) {
				Short levelId = parent.getCustomerDetail().getCustomerLevelId();
				if (levelId.equals(CustomerLevel.CLIENT.getValue())) {
					try {
						bulkEntryBusinessService.setClientAttendance(parent
								.getCustomerDetail().getCustomerId(),
								meetingDate, parent.getAttendence(), clients);
					} catch (ServiceException e) {
						customerNames.add(parent.getCustomerDetail()
								.getDisplayName());
						HibernateUtil.closeSession();
					} 
				}
			}
		} finally {
			HibernateUtil.closeSession();
			isThreadDone.append("Done");
		}
	}

}

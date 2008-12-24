package org.mifos.application.reports.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.reports.business.dto.CollectionSheetReportDTO;
import org.mifos.application.reports.business.dto.CollectionSheetReportData;
import org.mifos.framework.exceptions.ServiceException;


public interface ICollectionSheetReportService {

	public List<CollectionSheetReportDTO> getCollectionSheets(
			Integer branchId, Integer officerId, Integer centerId,
			Date meetingDate) throws Exception;
	
	public List<CollectionSheetReportData> getReportData(Integer branchId,
			String meetingDate, Integer personnelId, Integer centerId) throws ServiceException;
	
	public boolean displaySignatureColumn(Integer columnNumber)
	throws ServiceException;	
}

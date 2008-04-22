package org.mifos.application.reports.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.reports.business.dto.CollectionSheetReportDTO;


public interface ICollectionSheetReportService {

	public List<CollectionSheetReportDTO> getCollectionSheets(
			Integer branchId, Integer officerId, Integer centerId,
			Date meetingDate) throws Exception;
}

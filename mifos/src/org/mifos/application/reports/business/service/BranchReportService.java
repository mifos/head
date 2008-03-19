package org.mifos.application.reports.business.service;


import static org.mifos.framework.util.helpers.NumberUtils.convertIntegerToShort;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.branchreport.BranchReportClientSummaryBO;
import org.mifos.application.branchreport.BranchReportLoanArrearsAgingBO;
import org.mifos.application.branchreport.BranchReportLoanArrearsProfileBO;
import org.mifos.application.branchreport.BranchReportLoanDetailsBO;
import org.mifos.application.branchreport.BranchReportStaffSummaryBO;
import org.mifos.application.branchreport.BranchReportStaffingLevelSummaryBO;
import org.mifos.application.branchreport.LoanArrearsAgingPeriod;
import org.mifos.application.branchreport.persistence.BranchReportPersistence;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.reports.business.dto.BranchReportHeaderDTO;
import org.mifos.application.reports.util.helpers.ReportUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;

public class BranchReportService implements IBranchReportService {

	private OfficeBusinessService officeBusinessService;
	private PersonnelBusinessService personnelBusinessService;
	private BranchReportPersistence branchReportPersistence;

	public BranchReportService(OfficeBusinessService officeBusinessService,
			PersonnelBusinessService personnelBusinessService,
			BranchReportPersistence branchReportPersistence) {
		this.officeBusinessService = officeBusinessService;
		this.personnelBusinessService = personnelBusinessService;
		this.branchReportPersistence = branchReportPersistence;
	}

	public BranchReportService() {
		this(new OfficeBusinessService(), new PersonnelBusinessService(),
				new BranchReportPersistence());
	}

	public BranchReportHeaderDTO getBranchReportHeaderDTO(Integer branchId)
			throws ServiceException {
		Short officeId = convertIntegerToShort(branchId);
		PersonnelBO branchManager = CollectionUtils
				.getFirstElement(personnelBusinessService
						.getActiveBranchManagersUnderOffice(officeId));
		return new BranchReportHeaderDTO(officeBusinessService
				.getOffice(officeId), branchManager == null ? null
				: branchManager.getDisplayName());
	}

	public boolean isReportDataPresentForRundateAndBranchId(String branchId,
			String runDate) {
		try {
			return getBranchReport(Short.valueOf(branchId),
					ReportUtils.parseReportDate(runDate)) != null;
		}
		catch (ServiceException e) {
			return false;
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isReportDataPresentForRundate(Date runDate)
			throws ServiceException {
		try {
			List<BranchReportBO> branchReports = branchReportPersistence
					.getBranchReport(runDate);
			return branchReports != null && !branchReports.isEmpty();
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<BranchReportLoanArrearsAgingBO> getLoanArrearsAgingInfo(
			Integer branchId, String runDate) throws ServiceException {
		try {
			return branchReportPersistence.getLoanArrearsAgingReport(
					convertIntegerToShort(branchId), ReportUtils.parseReportDate(runDate));
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		catch (ParseException e) {
			throw new ServiceException(e);
		}
	}

	public List<BranchReportClientSummaryBO> getClientSummaryInfo(
			Integer branchId, String runDate) throws ServiceException {
		try {
			return branchReportPersistence.getBranchReportClientSummary(
					convertIntegerToShort(branchId), ReportUtils.parseReportDate(runDate));
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		catch (ParseException e) {
			throw new ServiceException(e);
		}
	}

	public List<BranchReportStaffingLevelSummaryBO> getStaffingLevelSummary(
			Integer branchId, String runDate) throws ServiceException {
		try {
			List<BranchReportStaffingLevelSummaryBO> staffingLevelSummary = branchReportPersistence
					.getBranchReportStaffingLevelSummary(
							convertIntegerToShort(branchId),
							ReportUtils.parseReportDate(runDate));
			Collections.sort(staffingLevelSummary);
			return staffingLevelSummary;
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		catch (ParseException e) {
			throw new ServiceException(e);
		}
	}

	public List<BranchReportStaffSummaryBO> getStaffSummary(Integer branchId,
			String runDate) throws ServiceException {
		try {
			return branchReportPersistence.getBranchReportStaffSummary(
					convertIntegerToShort(branchId), ReportUtils.parseReportDate(runDate));
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		catch (ParseException e) {
			throw new ServiceException(e);
		}
	}

	public List<BranchReportLoanDetailsBO> getLoanDetails(Integer branchId,
			String runDate) throws ServiceException {
		try {
			return branchReportPersistence.getLoanDetails(
					convertIntegerToShort(branchId), ReportUtils.parseReportDate(runDate));
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		catch (ParseException e) {
			throw new ServiceException(e);
		}
	}

	public List<BranchReportLoanArrearsProfileBO> getLoanArrearsProfile(
			Integer branchId, String runDate) throws ServiceException {
		try {
			return branchReportPersistence.getLoanArrearsProfile(
					convertIntegerToShort(branchId), ReportUtils.parseReportDate(runDate));
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		catch (ParseException e) {
			throw new ServiceException(e);
		}

	}

	public void removeBranchReport(BranchReportBO branchReport)
			throws ServiceException {
		try {
			branchReportPersistence.delete(branchReport);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<BranchReportBO> getBranchReports(Date runDate)
			throws ServiceException {
		try {
			return branchReportPersistence.getBranchReport(runDate);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public BranchReportBO getBranchReport(Short branchId, Date runDate)
			throws ServiceException {
		try {
			return CollectionUtils.getFirstElement(branchReportPersistence
					.getBranchReport(branchId, runDate));
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public void removeBranchReports(List<BranchReportBO> branchReports)
			throws ServiceException {
		for (BranchReportBO branchReport : branchReports) {
			removeBranchReport(branchReport);
		}
	}

	public BranchReportLoanArrearsAgingBO extractLoanArrearsAgingInfoInPeriod(
			Short officeId, LoanArrearsAgingPeriod loanArrearsAgingPeriod, MifosCurrency currency)
			throws ServiceException {
		try {
			return branchReportPersistence.extractLoanArrearsAgingInfoInPeriod(
					loanArrearsAgingPeriod, officeId, currency);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<BranchReportStaffSummaryBO> extractBranchReportStaffSummary(
			Short officeId, Integer daysInArrears, MifosCurrency currency) throws ServiceException {
		try {
			return branchReportPersistence
					.extractBranchReportStaffSummary(officeId, daysInArrears, currency);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public BigDecimal extractPortfolioAtRiskForOffice(OfficeBO office,
			Integer daysInArrears) throws ServiceException {
		try {
			return branchReportPersistence.extractPortfolioAtRiskForOffice(
					office, daysInArrears);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<BranchReportStaffingLevelSummaryBO> extractBranchReportStaffingLevelSummaries(
			Short branchId) throws ServiceException {
		try {
			return branchReportPersistence
					.extractBranchReportStaffingLevelSummary(branchId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}

	}

	public List<BranchReportLoanDetailsBO> extractLoanDetails(Short branchId, MifosCurrency currency)
			throws ServiceException {
		try {
			return branchReportPersistence.extractLoanDetails(branchId, currency);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public BranchReportLoanArrearsProfileBO extractLoansInArrearsCount(
			Short branchId, MifosCurrency currency) throws ServiceException {
		try {
			return branchReportPersistence
					.extractLoansArrearsProfileForBranch(branchId, currency);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

}

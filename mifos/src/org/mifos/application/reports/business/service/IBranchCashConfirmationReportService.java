package org.mifos.application.reports.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationCenterRecoveryBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationDisbursementBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationInfoBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationReportBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.exceptions.ServiceException;


public interface IBranchCashConfirmationReportService {

	public List<BranchCashConfirmationCenterRecoveryBO> getCenterRecoveries(
			Integer branchId, String runDate) throws ServiceException;


	public List<BranchCashConfirmationReportBO> extractBranchCashConfirmationReport(
			Date actionDate, AccountTypes accountType,
			List<Short> prdOfferingsForRecoveries,
			List<Short> prdOfferingsForIssues,
			List<Short> prdOfferingsForDisbursements, MifosCurrency currency,
			Date runDate) throws ServiceException;

	public List<BranchCashConfirmationReportBO> getBranchCashConfirmationReportsForDate(
			Date runDate) throws ServiceException;

	public void deleteBranchCashConfirmationReports(
			List<BranchCashConfirmationReportBO> reports)
			throws ServiceException;


	public List<BranchCashConfirmationInfoBO> getCenterIssues(Integer branchId,
			String runDate) throws ServiceException;


	public List<BranchCashConfirmationDisbursementBO> getDisbursements(
			Integer branchId, String runDate) throws ServiceException;

}

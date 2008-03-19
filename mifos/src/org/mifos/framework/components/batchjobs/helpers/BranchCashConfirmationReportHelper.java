package org.mifos.framework.components.batchjobs.helpers;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationReportBO;
import org.mifos.application.reports.business.service.BranchCashConfirmationConfigService;
import org.mifos.application.reports.business.service.IBranchCashConfirmationReportService;
import org.mifos.application.reports.business.service.ReportServiceFactory;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;

public class BranchCashConfirmationReportHelper extends TaskHelper {

	private IBranchCashConfirmationReportService service;
	private BranchCashConfirmationConfigService configService;

	public BranchCashConfirmationReportHelper(MifosTask mifosTask) {
		this(mifosTask, ReportServiceFactory
				.getBranchCashConfirmationReportService(null),
				ReportServiceFactory.getBranchCashConfirmationConfigService());
	}

	public BranchCashConfirmationReportHelper(MifosTask task,
			IBranchCashConfirmationReportService service,
			BranchCashConfirmationConfigService configService) {
		super(task);
		this.service = service;
		this.configService = configService;
	}

	public BranchCashConfirmationReportHelper(MifosTask task,
			BranchCashConfirmationConfigService configService) {
		this(task, ReportServiceFactory
				.getBranchCashConfirmationReportService(null), configService);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			extractAndSaveReportBO(session, DateUtils
					.getDateWithoutTimeStamp(timeInMillis));
			transaction.commit();
		}
		catch (HibernateException e) {
			transaction.rollback();
			throw new BatchJobException(e);
		}
		catch (ServiceException e) {
			transaction.rollback();
			throw new BatchJobException(e);
		}
	}

	void extractAndSaveReportBO(Session session, Date runDate)
			throws ServiceException {
		deleteCashConfirmationReportsIfExists(runDate);
		List<BranchCashConfirmationReportBO> branchCashConfirmationReports = extractCashConfirmationReport(runDate);
		for (BranchCashConfirmationReportBO report : branchCashConfirmationReports) {
			session.save(report);
		}
	}

	List<BranchCashConfirmationReportBO> extractCashConfirmationReport(
			Date runDate) throws ServiceException {
		return service
				.extractBranchCashConfirmationReport(configService.getActionDate(),
						AccountTypes.LOAN_ACCOUNT, configService
				.getProductOfferingsForRecoveries(),
						configService.getProductOfferingsForIssues(),
						configService.getProductOfferingsForDisbursements(),
						configService.getCurrency(), runDate);
	}

	private void deleteCashConfirmationReportsIfExists(Date today)
			throws ServiceException {
		service.deleteBranchCashConfirmationReports(service
				.getBranchCashConfirmationReportsForDate(today));
	}
}

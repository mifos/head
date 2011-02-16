<%@page import="org.mifos.platform.accounting.dao.AccountingDaoImpl"%><%@page import="org.mifos.platform.accounting.service.AccountingDataCacheManager"%><%@page import="java.util.Date"%><%@page import="org.mifos.platform.accounting.AccountingDto"%><%@page import="java.util.List"%><%@page import="org.mifos.platform.accounting.service.IAccountingService"%><%@page import="org.mifos.platform.accounting.service.AccountingServiceImpl"%><%@page import="org.joda.time.LocalDate"%><%@page import="org.joda.time.format.DateTimeFormat"%><%@page import="org.joda.time.format.DateTimeFormatter"%><%@page import="org.apache.commons.lang.StringUtils"%><%
	String paramToDate = request.getParameter("toDate");
	String paramFromDate = request.getParameter("fromDate");
	if (StringUtils.isBlank(paramToDate) || StringUtils.isBlank(paramFromDate)) {
		response.getWriter().print("WRONG PARAMS");
		return;
	}
	DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
	LocalDate fromDate = fmt.parseDateTime(paramFromDate).toLocalDate();
	LocalDate toDate = fmt.parseDateTime(paramToDate).toLocalDate();
	IAccountingService accountingService = new AccountingServiceImpl(new AccountingDataCacheManager(), new AccountingDaoImpl());
	String tallyXML = "FAILED " + (new Date()).toString();
	try {
		tallyXML = accountingService.getExportOutput(fromDate, toDate);
	} catch (Exception e) {
		e.printStackTrace();
	}
	String fileName = accountingService.getExportOutputFileName(fromDate, toDate);
	response.setHeader("Content-Disposition","attachment;filename=\"" + fileName);
	response.setContentType("application/octet-stream");
    out.print(tallyXML);
%>
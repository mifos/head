package org.mifos.framework.security.util;


public class ReportActionSecurity extends ActionSecurity {
	
	private final String method;

	public ReportActionSecurity(String name, String method) {
		super(name);
		this.method = method;
	}

	/**
	 * Allow report to a specified security constant (aka activity id)
	 */
	public void allowReport(int reportId, short securityConstant) {
		allow(this.method + "-" + reportId, securityConstant);
	}

}

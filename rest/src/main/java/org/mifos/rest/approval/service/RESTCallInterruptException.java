package org.mifos.rest.approval.service;

public class RESTCallInterruptException extends Exception {
	/**
    *
    */
   private static final long serialVersionUID = 1L;
	
    private final Long approvalId;

	public RESTCallInterruptException(Long id) {
    	approvalId = id;
	}

	public Long getApprovalId() {
		return approvalId;
	}

}

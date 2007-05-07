package org.mifos.framework.hibernate.helper;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;

public class SessionHolder {

	private final Session session;
	private Transaction transaction=null;
	private AuditInterceptor interceptor = null;
	
	public SessionHolder(Session session) {
		this.session = session;
		if (session == null) {
			throw new NullPointerException("session is required");
		}
		//interceptor = new AuditInterceptor();
	}
	
	public Transaction startTransaction() {
		if (transaction == null) {
			transaction = session.beginTransaction();
		}
		
		return transaction;
	}
	
	public void setTranasction(Transaction transaction) {
		this.transaction=transaction;
	}
	
	public Transaction getTransaction() {
		return transaction;	
	}
	
	public Session getSession() {
		return session;
	}
	
	public void setInterceptor(AuditInterceptor auditInterceptor){
		this.interceptor=auditInterceptor;
	}
	
	public AuditInterceptor getInterceptor(){
		return interceptor;
	}
}

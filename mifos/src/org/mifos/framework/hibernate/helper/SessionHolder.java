package org.mifos.framework.hibernate.helper;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;

public class SessionHolder {

	private Session session=null;
	private Transaction transaction=null;
	private AuditInterceptor interceptor = null;
	
	public SessionHolder(Session session) {
		this.session=session;
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

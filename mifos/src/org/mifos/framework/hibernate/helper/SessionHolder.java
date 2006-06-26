package org.mifos.framework.hibernate.helper;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class SessionHolder {

	private Session session=null;
	private Transaction transaction=null;
	
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
}

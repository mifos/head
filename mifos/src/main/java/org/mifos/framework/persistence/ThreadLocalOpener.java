package org.mifos.framework.persistence;

import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.SessionHolder;

public class ThreadLocalOpener implements SessionOpener {

	/**
	 * In the thread local case, we might be opening a new
	 * session, or returning one which is already open.
	 */
	public SessionHolder open() {
		return HibernateUtil.getOrCreateSessionHolder();
	}

}

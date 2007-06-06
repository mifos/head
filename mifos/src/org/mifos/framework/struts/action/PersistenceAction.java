package org.mifos.framework.struts.action;

import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.persistence.SessionOpener;
import org.mifos.framework.persistence.ThreadLocalOpener;

public class PersistenceAction extends BaseAction {
	
	private static SessionOpener defaultSessionOpener = new ThreadLocalOpener();
	protected SessionOpener opener;

	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
	}

	public static void setDefaultSessionOpener(SessionOpener defaultSessionOpener) {
		PersistenceAction.defaultSessionOpener = defaultSessionOpener;
	}
	
	public static void resetDefaultSessionOpener() {
		defaultSessionOpener = new ThreadLocalOpener();
	}
	
	public PersistenceAction() {
		opener = PersistenceAction.defaultSessionOpener;
	}

}
package org.mifos.framework.persistence;

import org.mifos.framework.hibernate.helper.SessionHolder;

public interface SessionOpener {

	public SessionHolder open();

}

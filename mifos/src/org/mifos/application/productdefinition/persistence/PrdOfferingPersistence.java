package org.mifos.application.productdefinition.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class PrdOfferingPersistence extends Persistence {

	public Short getMaxPrdOffering() throws PersistenceException {
		try {
			return (Short) HibernateUtil.getSessionTL().getNamedQuery(
					NamedQueryConstants.PRODUCTOFFERING_MAX).uniqueResult();
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public PrdStatusEntity getPrdStatus(PrdStatus prdStatus)
			throws PersistenceException {
		try {
			Session session = HibernateUtil.getSessionTL();
			PrdStatusEntity prdStatusEntity = (PrdStatusEntity) session.get(
					PrdStatusEntity.class, prdStatus.getValue());
			return prdStatusEntity;
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}
}

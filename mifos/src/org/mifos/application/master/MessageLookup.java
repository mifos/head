package org.mifos.application.master;

import org.hibernate.Session;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;

/**
 * This class looks up messages
 * from tables like {@link LookUpValueEntity}, {@link LookUpValueLocaleEntity}
 * and the like.
 * 
 * The idea is that we'll be able to come up with a simpler mechanism 
 * than the rather convoluted
 * one in {@link MasterPersistence}, {@link MasterDataEntity}, etc.
 * Or at least we can centralize where we call the convoluted
 * mechanism.
 */
public class MessageLookup {

	public static String lookup(WeekDay weekDay, UserContext user) {
		Session session = null;
		try {
			session = HibernateUtil.openSession();
			WeekDaysEntity entity = (WeekDaysEntity) 
				session.get(WeekDaysEntity.class, weekDay.getValue());
			entity.setLocaleId(user.getLocaleId());
			return entity.getName();
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

}

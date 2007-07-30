package org.mifos.application.acceptedpaymenttype.persistence;


import java.util.HashMap;
import java.util.List;
import org.mifos.application.acceptedpaymenttype.business.AcceptedPaymentType;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

	public class AcceptedPaymentTypePersistence extends Persistence {

		public AcceptedPaymentType getAcceptedPaymentType(Short paymentTypeId) {
			Session session = HibernateUtil.getSessionTL();
			return (AcceptedPaymentType) session.get(AcceptedPaymentType.class, paymentTypeId);
		}
		
		public List<AcceptedPaymentType> getAcceptedPaymentTypesForAnAccountAction(Short accountActionId ) throws PersistenceException {
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("accountActionId", accountActionId);
			
			return executeNamedQuery(
				NamedQueryConstants.GET_ACCEPTED_PAYMENT_TYPES_FOR_AN_ACCOUNT_ACTION, queryParameters);
		}
		
		public void addAcceptedPaymentTypes(List<AcceptedPaymentType> acceptedPaymentTypeList) throws PersistenceException {
			for (AcceptedPaymentType paymentType : acceptedPaymentTypeList)
				createOrUpdate(paymentType);
		}


		// delete a list of accepted payment type for account actions
		public void deleteAcceptedPaymentTypes(List<AcceptedPaymentType> acceptedPaymentTypeList) throws PersistenceException {
			for (AcceptedPaymentType paymentType : acceptedPaymentTypeList)
				delete(paymentType);
		}

		
	}


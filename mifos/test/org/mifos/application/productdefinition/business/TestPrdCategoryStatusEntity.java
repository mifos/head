package org.mifos.application.productdefinition.business;

import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestPrdCategoryStatusEntity extends MifosTestCase {
	
	private PrdCategoryStatusEntity prdCategoryStatusEntity;
	private Session session;

	@Override
	protected void setUp() throws Exception {
		session = HibernateUtil.getSessionTL();
	}
	
	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		session=null;		
	}
	
	public void testGetNameFailure() {
		prdCategoryStatusEntity = getPrdCategoryStatus(Short.valueOf("0"));
		String name = prdCategoryStatusEntity.getName(Short.valueOf("1"));
		assertFalse("This should fail, name is Inactive", !("Inactive".equals(name)));
	}
	
	public void testGetNameSuccess() {
		prdCategoryStatusEntity = getPrdCategoryStatus(Short.valueOf("1"));
		String name = prdCategoryStatusEntity.getName(Short.valueOf("1"));
		assertEquals("Active",name);
	}
	
	public void testGetNamesSuccess() {
		prdCategoryStatusEntity = getPrdCategoryStatus(Short.valueOf("1"));
		Set<LookUpValueLocaleEntity> lookUpValueLocaleEntitySet = prdCategoryStatusEntity.getNames();
		int size = lookUpValueLocaleEntitySet.size();
		assertEquals(1,size);
	}
	
	public void testGetNamesFailure() {
		prdCategoryStatusEntity = getPrdCategoryStatus(Short.valueOf("1"));
		Set<LookUpValueLocaleEntity> lookUpValueLocaleEntitySet = prdCategoryStatusEntity.getNames();
		int size = lookUpValueLocaleEntitySet.size();
		assertFalse("This should fail, the size is 1", !(size == 1));
	}
	
	private PrdCategoryStatusEntity getPrdCategoryStatus(Short id) {
		Query query = session.createQuery(
			"from org.mifos.application.productdefinition.business.PrdCategoryStatusEntity prdCatStatus " +
			"where prdCatStatus.id = ?");
		query.setString(0,id.toString());
		PrdCategoryStatusEntity prdCategoryStatusEntity = (PrdCategoryStatusEntity) query.uniqueResult();
		return prdCategoryStatusEntity;
	}


}

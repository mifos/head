package org.mifos.application.moratorium.business;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class MoratoriumBOTest
        extends MifosTestCase {

private MoratoriumBO moratoriumBO = null;	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		moratoriumBO = (MoratoriumBO) HibernateUtil.getSessionTL().get(
				MoratoriumBO.class, moratoriumBO.getMoratoriumId());
		
		TestObjectFactory.cleanUp(moratoriumBO);		
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testAddMoratorium()throws Exception{
		moratoriumBO = new MoratoriumBO();
		moratoriumBO.setAppliedTo("test get moratoriums");
		moratoriumBO.setStartDate(null);
		moratoriumBO.setEndDate(null);			
		moratoriumBO.setMorCreatedBy("created for test case");
		moratoriumBO.setNotes("created for test case");
		moratoriumBO.setCustomerId("1");
		moratoriumBO.setOfficeId("1");
		moratoriumBO.save();
		
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		moratoriumBO = (MoratoriumBO) HibernateUtil.getSessionTL().get(
				MoratoriumBO.class, moratoriumBO.getMoratoriumId());
		
		assertEquals("test get moratoriums", moratoriumBO.getAppliedTo());
		assertEquals("1", moratoriumBO.getCustomerId());
		assertEquals("1", moratoriumBO.getOfficeId());
	}
}

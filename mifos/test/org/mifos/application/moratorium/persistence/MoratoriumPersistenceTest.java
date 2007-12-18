package org.mifos.application.moratorium.persistence;

import java.util.List;

import org.mifos.application.moratorium.business.MoratoriumBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class MoratoriumPersistenceTest
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
	
	public void testGetMoratoriums() throws Exception {
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
		
		List<MoratoriumBO> moratoriums = new MoratoriumPersistence().getMoratoriums();
		assertNotNull(moratoriums);
		assertEquals(1, moratoriums.size());
	}
	
	public void testGetMoratoriumById() throws Exception{
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
		
		List<MoratoriumBO> moratorium = new MoratoriumPersistence().getMoratoriumById("2");
		assertNotNull(moratorium);
		assertEquals(1, moratorium.size());
	}
	
	public void testGetMoratoriumByCustomerId() throws Exception{
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
		
		List<MoratoriumBO> moratorium = new MoratoriumPersistence().getMoratoriumByCustomerId("1");
		assertNotNull(moratorium);
		assertEquals(1, moratorium.size());
	}
	
	public void testGetMoratoriumByOfficeId() throws Exception{
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
		
		List<MoratoriumBO> moratorium = new MoratoriumPersistence().getMoratoriumByOfficeId("1");
		assertNotNull(moratorium);
		assertEquals(1, moratorium.size());
	}
}

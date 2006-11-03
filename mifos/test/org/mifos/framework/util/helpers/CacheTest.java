package org.mifos.framework.util.helpers;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.QueryResultDTOImpl;

public class CacheTest extends MifosTestCase {

	private Cache cache = null;

	private QueryResultDTOImpl queryResult = null;

	private Query query = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cache = new Cache();
		queryResult = new QueryResultDTOImpl();
		Session session = null;
		QueryResult notesResult = null;
		notesResult = QueryFactory.getQueryResult("NotesSearch");
		session = notesResult.getSession();
		Query query = session
				.getNamedQuery(NamedQueryConstants.GETALLACCOUNTNOTES);
		query.setInteger("accountId", 1);
		queryResult.executeQuery(query);
		cache.setCacheMap(cache.getCacheMap());

		cache = new Cache(queryResult);
	}

	public void testGetCache() throws Exception {
		List list = cache.getList(1, "newMethod");
		list = cache.getList(3, "previous");
		assertNull(list);
		list = cache.getList(4, "previous");
		assertNull(list);
		assertEquals(0, cache.getSize());
		assertEquals(1, cache.getCacheMap().size());

		list = cache.getList(4, "next");
		cache.setSize(2);
		assertEquals(2, cache.getSize());
	}
}

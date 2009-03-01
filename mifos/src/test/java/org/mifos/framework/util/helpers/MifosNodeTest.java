package org.mifos.framework.util.helpers;

import java.util.HashMap;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class MifosNodeTest extends MifosTestCase {
	public MifosNodeTest() throws SystemException, ApplicationException {
        super();
    }

    public void testMifosNode(){
		HashMap nodes = new HashMap();
		nodes.put("key","value");
		MifosNode mifosNode = new MifosNode(nodes);
		assertEquals("value",mifosNode.getElement("key"));
		assertEquals("org.mifos.framework.util.helpers.Node",mifosNode.toString());
		mifosNode = new MifosNode();
		mifosNode.setNodes(nodes);
		assertEquals(1,mifosNode.getNodes().size());
	}
}

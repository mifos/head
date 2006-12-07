package org.mifos.framework.util.helpers;

import org.mifos.framework.TestUtils;
import junit.framework.TestCase;

public class FlowManagerTest extends TestCase {
	public void testIsSerializable() throws Exception {
		FlowManager flowManager = new FlowManager();
		TestUtils.assertCanSerialize(flowManager);
	}	
}

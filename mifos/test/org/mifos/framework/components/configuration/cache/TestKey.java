package org.mifos.framework.components.configuration.cache;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;


public class TestKey extends TestCase {

	public void testEquals() throws Exception {
		Key equal1 = new Key((short)1,"a");
		Key equal2 = new Key((short)1,"a");
		Key equal3 = new Key((short)1,"A");
		Key notEqual1 = new Key((short)1,"b");
		Key notEqual2 = new Key((short)2,"a");
		Key notEqual3 = new Key(null,null);
		Key subclass = new Key((short)1,"a") { };
		
		TestUtils.verifyBasicEqualsContract(
			new Key[]{equal1, equal2, equal3}, 
			new Key[]{notEqual1, notEqual2, notEqual3, subclass});
	}

}

package org.mifos.framework;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestUtils {

	/**
	 * Also see {@link TestObjectFactory#getUserContext()} which should be
	 * slower (it involves several database accesses).
	 */
	public static UserContext makeUser(int role) {
		UserContext user = new UserContext();
		user.setId(new Short("1"));
		user.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add((short) role);
		user.setRoles(set);
		user.setLevelId(Short.valueOf("2"));
		user.setName("mifos");
		user.setPereferedLocale(new Locale("en", "US"));
		user.setBranchId(new Short("1"));
		user.setBranchGlobalNum("0001");
		return user;
	}

	public static void assertWellFormedFragment(String xml) 
	throws DocumentException {
		SAXReader reader = new SAXReader();
	    reader.read(new StringReader("<root>" + xml + "</root>"));
	}

}

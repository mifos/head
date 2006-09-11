package org.mifos.framework;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.framework.security.util.UserContext;

public class TestUtils {

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

}

/**

 * MifosMifosTestCase.java    version: xxx



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.framework;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import org.hibernate.jmx.StatisticsService;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestCaseInitializer;

/**
 * Inheriting from this instead of TestCase is deprecated,
 * generally speaking.  The reason is that TestCaseInitializer
 * (a) runs too soon (it is more graceful for a long delay
 * to happen in setUp), and (b) initializes too much (most
 * tests don't need everything which is there).
 */
public class MifosTestCase extends TestCase {
	static {
		try {
			Class.forName(TestCaseInitializer.class.getName());
		} catch (ClassNotFoundException e) {
			throw new Error("Failed to start up", e);
		}
	}

    private StatisticsService statisticsService;

    public void assertEquals(String s , Money one , Money two)
	{
		if(one.equals(two))
			return;
		 throw new ComparisonFailure(s,one.toString(),two.toString());
	}
	
	public Date getDate(String date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.parse(date);
	}

    public StatisticsService getStatisticsService() {
        return this.statisticsService;
    }
    public void setStatisticsService(StatisticsService service) {
        this.statisticsService = service;
    }

    protected void initializeStatisticsService() {
        statisticsService = new StatisticsService();
        statisticsService.setSessionFactory(HibernateUtil.getSessionFactory());
        statisticsService.setStatisticsEnabled(true);
    }
}

package org.mifos.accounts.fees.struts.action;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringContextLoaderIntegrationTest extends TestCase {

    public SpringContextLoaderIntegrationTest() throws Exception {
        super();
    }

    public void testShouldLoadSpringContext() {
        ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:/org/mifos/config/resources/FeeContext.xml");
        Assert.assertTrue(ctx.getBean("feeServiceFacade") != null);

    }

}

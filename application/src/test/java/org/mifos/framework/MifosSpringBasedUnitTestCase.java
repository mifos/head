package org.mifos.framework;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/org/mifos/config/resources/unit-testContext.xml"})
public class MifosSpringBasedUnitTestCase {

}

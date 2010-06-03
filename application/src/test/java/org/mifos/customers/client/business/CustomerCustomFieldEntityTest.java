package org.mifos.customers.client.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerCustomFieldEntityTest {

    @Test
    public void shouldConvertCustomFieldDateToUniformPattern() throws InvalidDateException {
        List<CustomerCustomFieldEntity> customFields = Arrays.asList(
                new CustomerCustomFieldEntity(Short.valueOf((short) 1), "15/12/2010", CustomFieldType.DATE.getValue(), null));
        CustomerCustomFieldEntity.convertCustomFieldDateToUniformPattern(customFields, Locale.UK);
        assertThat(customFields.get(0).getFieldValue(), is("2010-12-15"));
    }
}

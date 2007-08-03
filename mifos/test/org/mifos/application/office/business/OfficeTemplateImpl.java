package org.mifos.application.office.business;

import java.util.List;
import java.util.ArrayList;

import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * <p/>
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license
 * and how it is applied.
 */
public class OfficeTemplateImpl implements OfficeTemplate {
    private Address address;
    private String officeName;
    private String shortName;
    private List<CustomFieldView> customFieldViews;
    private Short parentOfficeId;
    private OfficeLevel officeLevel;

    private OfficeTemplateImpl(OfficeLevel officeLevel) {
        this.officeLevel = officeLevel;
        address = new Address(
                "Address Line1", null, null, "Seattle",
                "WA", "USA", "98117", "206-555-1212");
        this.officeName = "TestOfficeName";
        this.shortName = "TON";
        this.parentOfficeId = TestObjectFactory.HEAD_OFFICE;

        this.customFieldViews = new ArrayList<CustomFieldView>();
		CustomFieldView customFieldView = new CustomFieldView();
		customFieldView.setFieldId(Short.valueOf("1"));
		customFieldView.setFieldValue("CustomField1Value");
		customFieldViews.add(customFieldView);
    }

    public OfficeLevel getOfficeLevel() {
        return this.officeLevel;
    }

    public String getOfficeName() {
        return officeName;
    }

    public Short getParentOfficeId()
    {
        return parentOfficeId;
    }
    public void setParentOfficeId(Short id) {
        this.parentOfficeId = id;
    }

    public String getShortName() {
        return shortName;
    }

    public Address getOfficeAddress() {
        return address;
    }

    public List<CustomFieldView> getCustomFieldViews() {
        return customFieldViews;
    }

    public OperationMode getOperationMode() {
        return OperationMode.REMOTE_SERVER;
    }

    /**
     * Use this in transactions that you don't plan on committing to the database.  If
     * you commit more than one of these to the database you'll run into uniqueness
     * constraints.  Plan on always rolling back the transaction.
     * @param officeLevel
     * @return
     */
    public static OfficeTemplateImpl createNonUniqueOfficeTemplate(OfficeLevel officeLevel) {
        return new OfficeTemplateImpl(officeLevel);
    }
}

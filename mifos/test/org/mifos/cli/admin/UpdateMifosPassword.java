package org.mifos.cli.admin;

import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.cli.BaseMifosCommandLine;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * <p/>
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
public class UpdateMifosPassword extends BaseMifosCommandLine {
    public static void main(String[] args)
            throws PersistenceException {
        System.out.println("changing password to " + args[0]);
        initializeApplication();
        System.out.println("application initialized");
        new PersonnelPersistence().getPersonnel((short)1).updatePassword(args[0], (short)1);
        HibernateUtil.commitTransaction();
        HibernateUtil.closeSession();
        System.out.println("done");
        System.exit(0);
    }
}

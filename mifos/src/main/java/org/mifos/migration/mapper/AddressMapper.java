/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.migration.mapper;

import org.mifos.migration.generated.Address;

public class AddressMapper {
	public static org.mifos.framework.business.util.Address mapXMLAddressToMifosAddress(Address address) {
		if (address == null) return null;
		
		return new org.mifos.framework.business.util.Address(
			address.getAddress1(),
			address.getAddress2(),
			address.getAddress3(),
			address.getCityDistrict(),
			address.getState(),
			address.getCountry(),
			address.getPostalCode(),
			address.getTelephone());			
	}
	
	public static Address mapMifosAddressToXMLAddress(org.mifos.framework.business.util.Address address) {
		if (address == null) return null;
		
		Address newAddress = new Address();
		newAddress.setAddress1(address.getLine1());
		newAddress.setAddress2(address.getLine2());
		newAddress.setAddress3(address.getLine3());
		newAddress.setCityDistrict(address.getCity());
		newAddress.setState(address.getState());
		newAddress.setCountry(address.getCountry());
		newAddress.setPostalCode(address.getZip());
		newAddress.setTelephone(address.getPhoneNumber());
				
		return newAddress;
	}
}

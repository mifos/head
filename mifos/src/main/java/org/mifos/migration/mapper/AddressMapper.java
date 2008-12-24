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

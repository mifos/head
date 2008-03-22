package org.mifos.framework.security.activity;

public enum DynamicLookUpValueCreationTypes {
	
		BirtReport((short) 1), CustomField((short)2), LookUpOption((short)3), DBUpgrade((short)4);

		Short value;

		DynamicLookUpValueCreationTypes(Short value) {
			this.value = value;
		}

		public Short getValue() {
			return value;
		}
	}



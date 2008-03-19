package org.mifos.framework.util;

import org.apache.commons.collections.Transformer;

public class TransformerUtils {

	public static final Transformer TRANSFORM_STRING_TO_SHORT = new Transformer() {
		public Object transform(Object input) {
			return Short.valueOf((String) input);
		}
	};
}

package org.mifos.framework.util.helpers;

public interface Transformer<I, O> {
	public O transform(I input);
}

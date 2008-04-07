package org.mifos.framework.util.helpers;

public interface Predicate<T> {
	public boolean evaluate(T object) throws Exception;
}

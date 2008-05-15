package org.mifos.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.customer.client.business.LoanCounter;
import org.mifos.framework.util.helpers.Predicate;
import org.mifos.framework.util.helpers.Transformer;

public class CollectionUtils {

	// Same as commons-collections but with generics
	public static <T> T find(Collection<T> collections, Predicate<T> predicate)
			throws Exception {
		if (collections != null && predicate != null) {
			for (T item : collections) {
				if (predicate.evaluate(item))
					return item;
			}
		}
		return null;
	}

	public static <T> boolean exists(Collection<T> collections,
			Predicate<T> predicate) throws Exception {
		return null != find(collections, predicate);
	}

	public static <T> List<T> asList(T ... elements) {
		List<T> list = new ArrayList<T>();
		for (T element : elements) {
			list.add(element);
		}
		return list;
	}

	public static <T> Set<T> asSet(T element) {
		Set<T> collectionSheetsForMeetingDate = new HashSet<T>();
		collectionSheetsForMeetingDate.add(element);
		return collectionSheetsForMeetingDate;
	}

	public static<T> T first(Collection<T> collection) {
		if(collection==null || collection.isEmpty()) return null;
		return collection.iterator().next();
	}

	public static <T> T last(Collection<T> collection) {
		if (collection == null || collection.isEmpty())
			return null;
		Iterator<T> iterator = collection.iterator();
		T elem = null;
		while (iterator.hasNext()) {
			elem = iterator.next();
		}
		return elem;
	}
	
	public static <T> T last(List<T> list) {
		if (list == null || list.isEmpty())
			return null;
		return list.get(list.size() - 1);
	}

	public static <T> Collection<T> select(Collection<T> collection,
			Predicate<T> predicate) throws Exception {
		Collection<T> outputCollection = new ArrayList<T>();
		select(collection, predicate, outputCollection);
		return outputCollection;
	}

	public static <T> void select(Collection<T> collection,
			Predicate<T> predicate, Collection<T> outputCollection)
			throws Exception {
		if (collection != null && predicate != null) {
			for (T item : collection) {
				if (predicate.evaluate(item)) {
					outputCollection.add(item);
				}
			}
		}
	}

	public static <T, O> Collection<O> select(Collection<T> collection,
			Predicate<T> predicate, Transformer<T, O> transformer) throws Exception {
		Collection<T> selectedCollection = select(collection, predicate);
		Collection<O> outputCollection = new ArrayList<O>();
		for (T item : selectedCollection) {
			outputCollection.add(transformer.transform(item));
		}
		return outputCollection;
	}
}


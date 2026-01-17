package com.druvu.lib.jdbc.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * Can be useful in @{@link com.druvu.lib.jdbc.statement.SqlStatement#getParameters()} when you need to combine params from different places.
 * Also, to flat results if a mapper type is a collection.
 * Generally this is an array merger
 *
 * @author Deniss Larka
 * at 22 Jan 2021
 */
public final class ArrayUtils {

	private ArrayUtils() {
	}

	public static Object[] join(Object[] arr1, Object[] arr2) {
		Object[] result = new Object[arr1.length + arr2.length];
		int index = 0;
		for (Object elem : arr1) {
			result[index] = elem;
			index++;
		}
		for (Object elem : arr2) {
			result[index] = elem;
			index++;
		}
		return result;
	}

	public static Object[] join(Object[] arr1, Object obj) {
		Object[] result = new Object[arr1.length + 1];
		int index = 0;
		for (Object elem : arr1) {
			result[index] = elem;
			index++;
		}
		result[index] = obj;
		return result;
	}

	//to parse parameters and 'unfold' any collection or array to a raw Object array
	//to use it parameterised, you have to be sure all the elements are the same type, otherwise use Object
	public static <T> List<T> flat(Object... array) {
		Objects.requireNonNull(array);
		List<T> result = new ArrayList<>();
		for (Object element : array) {
			if (element instanceof Collection) {
				Collection<T> collection = (Collection<T>) element;
				result.addAll(flat(collection));
				continue;
			}
			if (element.getClass().isArray()) {
				result.addAll(flat(toArray(element)));
				continue;
			}
			result.add((T) element);
		}
		return result;
	}

	public static <T> List<T> flat(Collection argCollection) {
		List<T> result = new ArrayList<>();
		for (Object element : argCollection) {
			if (element instanceof Collection) {
				Collection<T> collection = (Collection<T>) element;
				result.addAll(flat(collection));
				continue;
			}
			if (element.getClass().isArray()) {
				final Object[] array = toArray(element);
				result.addAll(flat(array));
				continue;
			}
			result.add((T) element);
		}
		return result;
	}

	private static <T> T[] toArray(T val) {
		int length = Array.getLength(val);
		Object[] outputArray = new Object[length];
		for (int i = 0; i < length; ++i) {
			outputArray[i] = Array.get(val, i);
		}
		return (T[]) outputArray;
	}
}
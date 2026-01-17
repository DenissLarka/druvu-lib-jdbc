package com.druvu.lib.jdbc.internal;

import java.util.List;
import java.util.Optional;

/**
 * Useful to extract a single item from the list.
 *
 * @author Deniss Larka
 * <br/>at 09 Nov 2020
 */
public final class OptionalUtils {

	private OptionalUtils() {
	}

	public static <T> Optional<T> from(List<T> list) {
		if (list == null || list.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(list.get(0));
	}

	//make sure not more than one element
	public static <T> Optional<T> uniqueOpt(List<T> list) {
		return Optional.ofNullable(uniqueItem(list));
	}

	//make sure not more than one element
	public static <T> T uniqueItem(List<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		if (list.size() > 1) {
			throw new IllegalStateException("Forbidden condition for a list size:" + list.size());
		}
		return list.get(0);
	}
}

package com.druvu.lib.jdbc.internal;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Deniss Larka
 * <br/>on 19 Jan 2024
 */
public final class PlaceholderUtils {

	public static final String INCLUDE_PLACEHOLDER = "\\%s";
	static final Pattern INCLUDE_PLACEHOLDER_RE = Pattern.compile(INCLUDE_PLACEHOLDER);
	static final String EMPTY = "";

	private PlaceholderUtils() {
	}


	public static String[] resize(String[] array, int countLimit) {
		final String[] newArray = Arrays.copyOf(array, countLimit);
		for (int i = 0; i < newArray.length; i++) {
			//padding with empty strings
			if (newArray[i] == null) {
				newArray[i] = EMPTY;
			}
		}
		return newArray;
	}

	public static int countIncludePlaceholders(String sql) {
		return count(sql, INCLUDE_PLACEHOLDER_RE);
	}

	private static int count(String content, Pattern pattern) {
		final Matcher matcher = pattern.matcher(content);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}

}

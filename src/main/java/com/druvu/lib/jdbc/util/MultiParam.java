package com.druvu.lib.jdbc.util;

import java.util.StringJoiner;
import java.util.stream.IntStream;

/**
 * Simple replacement of predefined placeholder with provided number of "?" parameters
 *
 * @author Deniss Larka
 * <br/>on 25 Aug 2021
 */
public final class MultiParam {

	public static final int MAX_PARAM_LEN = 1024;

	public static final String MULTI_PARAM_PLACEHOLDER = "\\?\\?\\?";

	private MultiParam() {
	}

	public static String replace(String sqlContent, int parametersLength) {
		return sqlContent.replaceFirst(MULTI_PARAM_PLACEHOLDER, multiParameter(parametersLength)).trim();
	}

	static private String multiParameter(int parametersCount) {
		if (parametersCount > MAX_PARAM_LEN) {
			throw new IllegalArgumentException("Too many parameters: " + parametersCount);
		}
		StringJoiner joiner = new StringJoiner(",");
		IntStream.range(0, parametersCount).forEach(param -> joiner.add("?"));
		return joiner.toString();
	}
}

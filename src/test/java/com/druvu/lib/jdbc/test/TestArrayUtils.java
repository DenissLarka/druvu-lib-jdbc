package com.druvu.lib.jdbc.test;

import java.math.BigDecimal;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.druvu.lib.jdbc.util.ArrayUtils;

/**
 * @author Deniss Larka
 * at 22.Jan.2021
 */
public class TestArrayUtils {

	@Test
	public void test1() {

		final Object[] result =
				ArrayUtils.join(new Object[] {"one", 2, new BigDecimal("3")}, new Object[] {"four", 5, new BigDecimal("6")});
		Assert.assertEquals(result, new Object[] {"one", 2, new BigDecimal("3"), "four", 5, new BigDecimal("6")});

	}

	@Test
	public void testFlat() {

		final Object[] result =
				ArrayUtils.flat(
						"one",
						"two",
						Arrays.asList("three", Arrays.asList("four", "five")),
						new long[] {6, 7, 8, 9}).toArray();

		Assert.assertEquals(result, new Object[] {
				"one",
				"two",
				"three",
				"four",
				"five",
				Long.valueOf(6),
				Long.valueOf(7),
				Long.valueOf(8),
				Long.valueOf(9)});

	}

}
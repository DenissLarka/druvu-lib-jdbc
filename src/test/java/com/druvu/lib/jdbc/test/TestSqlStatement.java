package com.druvu.lib.jdbc.test;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.druvu.lib.jdbc.statement.SqlStatement;
import com.druvu.lib.jdbc.util.ArrayUtils;
import com.druvu.lib.jdbc.util.MultiParam;
import com.druvu.lib.jdbc.util.SqlDebug;
import com.druvu.lib.jdbc.util.SqlLoader;

/**
 * @author Deniss Larka
 * at 31 Jul 2020
 */
public class TestSqlStatement {

	@Test
	public void test1() {
		SqlStatement<String> sqlStatement = new SqlStatement<>() {
			@Override
			public Object[] getParameters() {
				return ArrayUtils.flat("one", "two", Arrays.asList("three", Arrays.asList("four", "five"))).toArray();
			}

			@Override
			public String getQuery() {
				final String sql = SqlLoader.load("sql/test5.sql");
				return MultiParam.replace(sql, 3);
			}
		};
		Assert.assertEquals(sqlStatement.getQuery(), "select col1, col2, col3 from table1 where col1=? and col2=? and col3 in (?,?,?)");
	}

	@Test
	public void testDebug() {
		SqlStatement<String> sqlStatement = new SqlStatement<>() {
			@Override
			public Object[] getParameters() {
				return new Object[] {"one", "two", Integer.valueOf(3)};
			}
		};
		String sql = SqlLoader.load("sql/test4.sql");
		sql = SqlDebug.debug(sql, sqlStatement.getParameters());
		Assert.assertEquals(sql, "select col1, col2, col3 from table1 where col1='one' and col2='two' and col3=3");
	}

	@Test
	public void testFromResource1() {
		final String sql = SqlLoader.load("sql/test.sql", "sql/include.sql");
		Assert.assertEquals(sql, "select col1, col2 from table1 where col1 in (?) and col2 in (1,2,3)");
	}

	@Test
	public void testFromResource2() {
		final String sql = SqlLoader.load("sql/test.sql");
		Assert.assertEquals(sql, "select col1, col2 from table1 where col1 in (?)");
	}

	@Test
	public void testFromResource3() {
		String sql = SqlLoader.load("sql/test2.sql");
		sql = MultiParam.replace(sql, 4);
		Assert.assertEquals(sql, "select col1, col2 from table1 where col1 in (?,?,?,?)");
	}

	@Test
	public void testFromResource4() {
		final String sql = SqlLoader.load("sql/test3.sql", null, "sql/include.sql");
		Assert.assertEquals(sql, "select col1, col2 from table1 where 1=1  and col2 in (1,2,3)");
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testFromResource5() {
		SqlLoader.load("sql/test.sql", "sql/include-not-exist.sql");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testFromResource6() {
		MultiParam.replace("???", 1026);
	}
}
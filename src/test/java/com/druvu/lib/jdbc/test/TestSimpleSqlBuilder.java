package com.druvu.lib.jdbc.test;

import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.druvu.lib.jdbc.SimpleSql;
import com.druvu.lib.jdbc.SqlStatement;
import com.druvu.lib.jdbc.internal.SimpleSqlBuilder;

/**
 * @author Deniss Larka
 */
public class TestSimpleSqlBuilder {

	@Test
	public void testFromStringNoParams() {
		SimpleSqlBuilder builder = SimpleSql.fromString("SELECT * FROM users");

		Assert.assertEquals(builder.getQuery(), "SELECT * FROM users");
		Assert.assertEquals(builder.getParameters().length, 0);
	}

	@Test
	public void testFromStringWithParams() {
		SimpleSqlBuilder builder = SimpleSql.fromString("SELECT * FROM users WHERE id = ? AND status = ?")
				.with(42, "active");

		Assert.assertEquals(builder.getQuery(), "SELECT * FROM users WHERE id = ? AND status = ?");
		Assert.assertEquals(builder.getParameters().length, 2);
		Assert.assertEquals(builder.getParameters()[0], 42);
		Assert.assertEquals(builder.getParameters()[1], "active");
	}

	@Test
	public void testChainedWith() {
		SimpleSqlBuilder builder = SimpleSql.fromString("SELECT * FROM t WHERE a = ? AND b = ? AND c = ?")
				.with(1)
				.with(2)
				.with(3);

		Assert.assertEquals(builder.getParameters().length, 3);
		Assert.assertEquals(builder.getParameters()[0], 1);
		Assert.assertEquals(builder.getParameters()[1], 2);
		Assert.assertEquals(builder.getParameters()[2], 3);
	}

	@Test
	public void testWithMultipleParams() {
		SimpleSqlBuilder builder = SimpleSql.fromString("SELECT * FROM t WHERE a = ? AND b = ?")
				.with(1, 2);

		Assert.assertEquals(builder.getParameters().length, 2);
	}

	@Test
	public void testWithNullArgs() {
		SimpleSqlBuilder builder = SimpleSql.fromString("SELECT 1")
				.with((Object[]) null);

		Assert.assertEquals(builder.getParameters().length, 0);
	}

	@Test
	public void testWithNullValue() {
		SimpleSqlBuilder builder = SimpleSql.fromString("SELECT * FROM t WHERE a = ?")
				.with((Object) null);

		Assert.assertEquals(builder.getParameters().length, 1);
		Assert.assertNull(builder.getParameters()[0]);
	}

	@Test
	public void testMapToCustomType() {
		RowMapper<TestEntity> mapper = (rs, rowNum) -> new TestEntity();

		SqlStatement<TestEntity> statement = SimpleSql.fromString("SELECT id, name FROM users WHERE id = ?")
				.with(42)
				.map(mapper);

		Assert.assertEquals(statement.getQuery(), "SELECT id, name FROM users WHERE id = ?");
		Assert.assertEquals(statement.getParameters().length, 1);
		Assert.assertEquals(statement.getParameters()[0], 42);
		Assert.assertSame(statement.rowMapper(), mapper);
	}

	@Test
	public void testBuilderIsStatement() {
		// Builder extends SqlStatement, so it can be used directly
		SqlStatement<Map<String, Object>> statement = SimpleSql.fromString("SELECT * FROM users")
				.with(1);

		Assert.assertTrue(statement instanceof SimpleSqlBuilder);
	}

	@Test
	public void testFromResource() {
		SimpleSqlBuilder builder = SimpleSql.fromResource("sql/test4.sql")
				.with("value1", "value2", 3);

		Assert.assertEquals(builder.getQuery(), "select col1, col2, col3 from table1 where col1=? and col2=? and col3=?");
		Assert.assertEquals(builder.getParameters().length, 3);
	}

	private static class TestEntity {
	}
}
